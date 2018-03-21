package com.github.christophpickl.urclubs.myclubs.cache

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.CourseFilter
import com.github.christophpickl.urclubs.myclubs.HttpApi
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.UserMycJson
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
import java.time.Duration
import java.time.temporal.ChronoUnit.DAYS
import javax.inject.Inject

interface MyClubsCacheManager {
    fun clearCaches()
}

data class CacheSpec<T>(
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

    private val userSpec = CacheSpec(
            cacheAlias = "user",
            valueType = CachedUserMycJson::class.java,
            duration = Duration.of(2, DAYS),
            serializerType = CachedUserMycJsonSerializer::class.java,
            copierType = CachedUserMycJsonCopier::class.java
    )
    private val userCacheKey = "userKey"
    private val cacheSpecs = listOf(userSpec)

    init {
        log.debug { "Init cache" }
        var builder: CacheManagerBuilder<CacheManager> = CacheManagerBuilder.newCacheManagerBuilder()
        if (cacheDirectory != null) {
            log.debug { "Setting cache directory to: ${cacheDirectory.canonicalPath}" }
            @Suppress("UNCHECKED_CAST")
            builder = builder.with(CacheManagerBuilder.persistence(cacheDirectory)) as CacheManagerBuilder<CacheManager>
        }

        cacheSpecs.map { entity ->
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

        cacheSpecs.forEach {
            log.trace { "Removing cache: $it" }
            cacheManager.getFor(userSpec).clear()
        }
    }

    fun closeCache() {
        log.debug { "closeCache()" }
        cacheManager.close()
    }

    private fun <T> CacheManager.getFor(spec: CacheSpec<T>): Cache<String, T> =
            getCache(spec.cacheAlias, spec.keyType, spec.valueType)
                    ?: throw Exception("Could not find cache by: $spec")

    override fun loggedUser(): UserMycJson {
        log.trace { "loggedUser()" }
        val cache = cacheManager.getFor(userSpec)
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
