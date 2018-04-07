package com.github.christophpickl.urclubs.myclubs.cache

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.cache.entities.activitySpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.coursesSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.finishedActivitiesSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnerSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnersSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.userSpec
import org.ehcache.Cache
import org.ehcache.CacheManager
import org.ehcache.config.ResourcePools
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ExpiryPolicyBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import java.io.File

private val log = LOG {}

object CacheBuilder {

    val cacheSpecs = listOf<CacheSpec<*, *>>(
        userSpec,
        partnersSpec,
        partnerSpec,
        finishedActivitiesSpec,
        activitySpec,
        coursesSpec
        // ... add more here ...
    )

    private fun defaultResourcePools() = ResourcePoolsBuilder.newResourcePoolsBuilder()
        .heap(1, MemoryUnit.MB)
        .offheap(5, MemoryUnit.MB)
        .disk(50, MemoryUnit.MB, true)
        .build()

    fun build(cacheDirectory: File?, overrideResourcePools: ResourcePools?): CacheManager {

        var builder: CacheManagerBuilder<CacheManager> = CacheManagerBuilder.newCacheManagerBuilder()

        if (cacheDirectory != null) {
            log.debug { "Setting cache directory to: ${cacheDirectory.canonicalPath}" }
            @Suppress("UNCHECKED_CAST")
            builder = builder.with(CacheManagerBuilder.persistence(cacheDirectory)) as CacheManagerBuilder<CacheManager>
        }

        cacheSpecs.map {
            @Suppress("UNCHECKED_CAST")
            it as CacheSpec<Any, Any>
        }.map { entity: CacheSpec<Any, Any> ->
            log.debug { "Registering cache for: $entity" }
            builder = builder.withCache(entity.cacheAlias,
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    entity.keyType,
                    entity.valueType,
                    overrideResourcePools ?: defaultResourcePools()
                )
                    .withValueSerializer(entity.serializerType)
                    .withValueCopier(entity.copierType)
                    .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(entity.duration))
            )
        }

        return builder.build(true)
    }
}

fun <CACHE, MODEL> CacheManager.getFor(spec: CacheSpec<CACHE, MODEL>): Cache<String, CACHE> =
    getCache(spec.cacheAlias, spec.keyType, spec.valueType)
        ?: throw Exception("Could not find cache by: $spec (registered in list of cache specs??)")

fun <CACHED, MODEL> CacheManager.getOrPutSingledCache(
    delegate: MyClubsApi,
    spec: CacheSpec<CACHED, MODEL>,
    coordinates: SingleCacheCoordinates<CACHED, MODEL>
): MODEL {
    val cache = getFor(spec)

    cache.get(coordinates.staticKey)?.let {
        log.trace { "Cache hit for static cache key '${coordinates.staticKey}'" }
        return coordinates.transToModel(it)
    }

    log.debug { "$this Cache miss. Store in '${spec.cacheAlias}' with key '${coordinates.staticKey}'" }
    val result = coordinates.fetch(delegate)
    cache.put(coordinates.staticKey, coordinates.transToCache(result))
    return result
}

fun <CACHED, MODEL, REQUEST> CacheManager.getOrPutKeyCached(
    delegate: MyClubsApi,
    spec: CacheSpec<CACHED, MODEL>,
    coordinates: KeyedCacheCoordinates<CACHED, MODEL, REQUEST>
): MODEL {
    val cache = getFor(spec)

    cache.get(coordinates.cacheKey)?.let {
        log.debug { "Cache hit for cache key: '${coordinates.cacheKey}'" }
        return coordinates.toModel(it)
    }

    log.debug { "$this Cache miss. Store in '${spec.cacheAlias}' with key '${coordinates.cacheKey}'" }
    val result = coordinates.withDelegate(delegate)
    cache.put(coordinates.cacheKey, coordinates.toCache(result))
    return result
}
