package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import com.github.christophpickl.urclubs.myclubs.cache.ToCacheable
import com.github.christophpickl.urclubs.myclubs.cache.ToModelable
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.temporal.ChronoUnit

val activitySpec = CacheSpec(
    cacheAlias = "activityAlias",
    valueType = CachedActivityHtmlModel::class.java,
    duration = Duration.of(2, ChronoUnit.DAYS),
    serializerType = CachedActivityHtmlModelSerializer::class.java,
    copierType = CachedActivityHtmlModelCopier::class.java
)

data class ActivityHtmlModelWrapper(
    val wrapped: ActivityHtmlModel
) : ToCacheable<CachedActivityHtmlModel> {
    override fun toCache() = CachedActivityHtmlModel(wrapped)
}

data class CachedActivityHtmlModel(
    val partnerShortName: String?,
    val description: String?
) : ToModelable<ActivityHtmlModelWrapper> {

    @Suppress("unused") // needed for kryo
    constructor() : this(null, null)

    constructor(original: ActivityHtmlModel) : this(
        partnerShortName = original.partnerShortName,
        description = original.description
    )

    override fun toModel() = ActivityHtmlModelWrapper(
        ActivityHtmlModel(
            partnerShortName = partnerShortName!!,
            description = description!!
        )
    )
}

class CachedActivityHtmlModelSerializer(loader: ClassLoader? = null) : AbstractCachedSerializer<CachedActivityHtmlModel>(loader) {
    override val objectType = CachedActivityHtmlModel::class.java
}

class CachedActivityHtmlModelCopier : Copier<CachedActivityHtmlModel> {
    override fun copyForRead(obj: CachedActivityHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedActivityHtmlModel) = obj.copy()
}
