package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import com.github.christophpickl.urclubs.myclubs.cache.ToCacheable
import com.github.christophpickl.urclubs.myclubs.cache.ToModelable
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.temporal.ChronoUnit

val partnerSpec = CacheSpec<CachedPartnerDetailHtmlModel, PartnerDetailHtmlModelWrapper>(
    cacheAlias = "partnerAlias",
    valueType = CachedPartnerDetailHtmlModel::class.java,
    duration = Duration.of(2, ChronoUnit.DAYS),
    serializerType = CachedPartnerDetailHtmlModelSerializer::class.java,
    copierType = CachedPartnerDetailHtmlModelCopier::class.java
)

data class PartnerDetailHtmlModelWrapper(
    val wrapped: PartnerDetailHtmlModel
) : ToCacheable<CachedPartnerDetailHtmlModel> {
    override fun toCache() = CachedPartnerDetailHtmlModel(wrapped)
}

data class CachedPartnerDetailHtmlModel(
    val name: String?,
    val description: String?,
    val linkPartnerSite: String?,
    val addresses: List<String>?,
    val flags: List<String>?,
    val upcomingActivities: List<PartnerDetailActivityHtmlModel>?
) : ToModelable<PartnerDetailHtmlModelWrapper> {

    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null, null, null, null)

    constructor(original: PartnerDetailHtmlModel) : this(
        name = original.name,
        description = original.description,
        linkPartnerSite = original.linkPartnerSite,
        addresses = original.addresses,
        flags = original.flags,
        upcomingActivities = original.upcomingActivities
    )

    override fun toModel() = PartnerDetailHtmlModelWrapper(
        PartnerDetailHtmlModel(
            name = name!!,
            description = description!!,
            linkPartnerSite = linkPartnerSite!!,
            addresses = addresses!!,
            flags = flags!!,
            upcomingActivities = upcomingActivities!!
        )
    )
}

class CachedPartnerDetailHtmlModelSerializer(loader: ClassLoader? = null) : AbstractCachedSerializer<CachedPartnerDetailHtmlModel>(loader) {
    override val objectType = CachedPartnerDetailHtmlModel::class.java
}

class CachedPartnerDetailHtmlModelCopier : Copier<CachedPartnerDetailHtmlModel> {
    override fun copyForRead(obj: CachedPartnerDetailHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedPartnerDetailHtmlModel) = obj.copy()
}
