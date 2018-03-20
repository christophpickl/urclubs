package com.github.christophpickl.urclubs.myclubs

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.google.inject.BindingAnnotation
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.ResourcePools
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import org.ehcache.spi.copy.Copier
import org.ehcache.spi.serialization.Serializer
import java.io.File
import java.nio.ByteBuffer
import java.time.Duration
import java.time.temporal.ChronoUnit
import javax.inject.Inject

interface MyClubsCacheManager {
    fun clearCaches()
}

data class CacheEntity<T>(
        val cacheAlias: String,
        val valueType: Class<T>,
        val duration: Duration,
        val serializerType: Class<out Serializer<T>>,
        val copierType: Class<out Copier<T>>
) {
    val keyType = String::class.java
}

@Retention
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class CacheFile

class MyClubsCachedApi constructor(
        private val delegate: MyClubsApi,
        cacheDirectory: File?,
        overrideResourcePools: ResourcePools?
) : MyClubsApi, MyClubsCacheManager {

    companion object {
        private val defaultResourcePools = ResourcePoolsBuilder.newResourcePoolsBuilder()
                .heap(1, MemoryUnit.MB)
                .offheap(10, MemoryUnit.MB)
                .disk(50, MemoryUnit.MB, true)
                .build()
    }

    @Inject constructor(
            @HttpApi delegate: MyClubsApi,
            @CacheFile cacheDirectory: File

    ) : this(delegate, cacheDirectory, null)

    private val log = LOG {}


    private val cacheManager: CacheManager

    private val entityUser = CacheEntity(
            cacheAlias = "user",
            valueType = CachedUserMycJson::class.java,
            duration = Duration.of(2, ChronoUnit.DAYS),
            serializerType = CachedUserMycJsonSerializer::class.java,
            copierType = CachedUserMycJsonCopier::class.java
    )
    private val userCacheKey = "userKey"
    private val entities = listOf(entityUser)

    init {
        log.debug { "Init cache" }
        var builder: CacheManagerBuilder<CacheManager> = CacheManagerBuilder.newCacheManagerBuilder()
        if (cacheDirectory != null) {
            log.debug { "Setting cache directory to: ${cacheDirectory.canonicalPath}" }
            builder = builder.with(CacheManagerBuilder.persistence(cacheDirectory)) as CacheManagerBuilder<CacheManager>
        }

        entities.map { entity ->
            log.debug { "Registering cache for: $entity" }
            builder = builder.withCache(entity.cacheAlias,
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(
                            entity.keyType,
                            entity.valueType,
                            overrideResourcePools ?: defaultResourcePools
                    )
                            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(entity.duration))
                            .withValueSerializer(entity.serializerType)
                            .withValueCopier(entity.copierType)
            )
        }

        cacheManager = builder.build(true)
    }

    override fun clearCaches() {
        log.info { "clearCaches()" }

        entities.forEach {
            log.trace { "Removing cache: $it" }
            cacheManager.getFor(entityUser).clear()
        }
    }

    fun closeCache() {
        log.debug { "closeCache()" }
        cacheManager.close()
    }

    private fun <T> CacheManager.getFor(entity: CacheEntity<T>): Cache<String, T> =
            getCache(entity.cacheAlias, entity.keyType, entity.valueType)
                    ?: throw Exception("Could not find cache by: $entity")

    override fun loggedUser(): UserMycJson {
        log.trace { "loggedUser()" }
        val cache = cacheManager.getFor(entityUser)
        cache.get(userCacheKey)?.let {
            log.trace { "Cache hit" }
            return it.toUserMycJson()
        }
        val result = delegate.loggedUser()
        cache.put(userCacheKey, CachedUserMycJson(result))
        return result
    }

    override fun partners(): List<PartnerHtmlModel> {
        return delegate.partners()
    }

    override fun partner(shortName: String): PartnerDetailHtmlModel {
        return delegate.partner(shortName)
    }

    override fun courses(filter: CourseFilter): List<CourseHtmlModel> {
        return delegate.courses(filter)
    }

    override fun activity(filter: ActivityFilter): ActivityHtmlModel {
        return delegate.activity(filter)
    }

    override fun finishedActivities(): List<FinishedActivityHtmlModel> {
        return delegate.finishedActivities()
    }

}

data class CachedUserMycJson(
        val id: String?,
        val email: String?,
        val firstName: String?,
        val lastName: String?

) {
    // needed for kryo

    constructor() : this(null, null, null, null)
    constructor(original: UserMycJson) : this(
            id = original.id,
            email = original.email,
            firstName = original.firstName,
            lastName = original.lastName
    )

    fun toUserMycJson() = UserMycJson(
            id = id!!,
            email = email!!,
            firstName = firstName!!,
            lastName = lastName!!
    )
}

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
abstract class AbstractCachedSerializer<T>(@Suppress("UNUSED_PARAMETER") loader: ClassLoader) : Serializer<T> {

    private val kryo = Kryo()
    private val bufferSize = 4096

    override fun serialize(obj: T): ByteBuffer {
        val output = Output(bufferSize)
        kryo.writeObject(output, obj)
        return ByteBuffer.wrap(output.buffer)
    }

    override fun read(binary: ByteBuffer): T {
        val input = Input(ByteBufferInputStream(binary))
        return kryo.readObject(input, objectType)
    }

    override fun equals(obj: T, binary: ByteBuffer) = obj == read(binary)

    protected abstract val objectType: Class<T>
}

class CachedUserMycJsonSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedUserMycJson>(loader) {
    override val objectType = CachedUserMycJson::class.java
}

class CachedUserMycJsonCopier : Copier<CachedUserMycJson> {
    override fun copyForRead(obj: CachedUserMycJson) = obj.copy()
    override fun copyForWrite(obj: CachedUserMycJson) = obj.copy()
}
