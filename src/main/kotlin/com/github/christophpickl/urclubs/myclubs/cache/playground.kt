package com.github.christophpickl.urclubs.myclubs.cache

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.ByteBufferInputStream
import com.esotericsoftware.kryo.io.Input
import com.esotericsoftware.kryo.io.Output
import org.ehcache.Cache
import org.ehcache.CacheManager
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

// http://www.ehcache.org/documentation/3.4/index.html
class CacheDemo {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val demo = CacheDemo()
            val cache = demo.initCache()

            println("result1: ${cache.get("a")}")

            cache.put("a", CachedPartner.SAMPLE1)
            println("result2: ${cache.get("a")}")

            demo.cacheManager.close()
        }
    }

    lateinit var cacheManager: CacheManager

    private val urclubsHome = File(System.getProperty("user.home"), ".urclubs_dev/cache_dummy")

    fun initCache(): Cache<String, CachedPartner> {
        val partnersCacheAlias = "partnersCache"
        val cacheKeyType = String::class.java
        val cacheValueType = CachedPartner::class.java
        cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            .with(CacheManagerBuilder.persistence(File(urclubsHome, "cache")))
            .withCache(partnersCacheAlias,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(cacheKeyType, cacheValueType,
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(1, MemoryUnit.MB)
                        .offheap(10, MemoryUnit.MB)
                        .disk(50, MemoryUnit.MB, true)
                        .build()
                )
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.of(2, ChronoUnit.DAYS)))
                    .withValueSerializer(CachedPartnerSerializer::class.java)
                    .withValueCopier(CachedPartnerCopier::class.java)
            )
            .build(true)

        return cacheManager.getCache(partnersCacheAlias, cacheKeyType, cacheValueType)
    }
}

class CachedPartnerCopier : Copier<CachedPartner> {
    override fun copyForRead(obj: CachedPartner) = obj.copy()
    override fun copyForWrite(obj: CachedPartner) = obj.copy()
}

// http://www.ehcache.org/blog/2016/05/12/ehcache3-serializers.html#third-party-serializers
class CachedPartnerSerializer(@Suppress("UNUSED_PARAMETER") loader: ClassLoader) : Serializer<CachedPartner> {

    private val kryo = Kryo()
    private val bufferSize = 4096

    override fun serialize(obj: CachedPartner): ByteBuffer {
        val output = Output(bufferSize)
        kryo.writeObject(output, obj)
        return ByteBuffer.wrap(output.buffer)
    }

    override fun read(binary: ByteBuffer): CachedPartner {
        val input = Input(ByteBufferInputStream(binary))
        return kryo.readObject(input, CachedPartner::class.java)
    }

    override fun equals(obj: CachedPartner, binary: ByteBuffer) = obj == read(binary)

}

data class CachedPartner(
    val id: Long?,
    val html: String?
) {
    // needed for kryo
    constructor() : this(null, null)

    companion object {
        val SAMPLE1 = CachedPartner(1, "asdf111")
        val SAMPLE2 = CachedPartner(2, "asdf222")
    }
}
