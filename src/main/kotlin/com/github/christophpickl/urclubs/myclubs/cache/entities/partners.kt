package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import com.github.christophpickl.urclubs.myclubs.cache.ToCacheable
import com.github.christophpickl.urclubs.myclubs.cache.ToModelable
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.temporal.ChronoUnit

val partnersSpec: CacheSpec<CachedPartnersHtmlModel> = CacheSpec(
    cacheAlias = "partnersAlias",
    valueType = CachedPartnersHtmlModel::class.java,
    duration = Duration.of(2, ChronoUnit.DAYS),
    serializerType = CachedPartnersHtmlModelSerializer::class.java,
    copierType = CachedPartnersHtmlModelCopier::class.java
)

data class PartnersHtmlModelWrapper(
    val wrapped: List<PartnerHtmlModel>
) : ToCacheable<CachedPartnersHtmlModel> {
    override fun toCache() = CachedPartnersHtmlModel.byOriginal(wrapped)
}

data class CachedPartnersHtmlModel(
    val partners: List<CachedPartnerHtmlModel>?
) : ToModelable<PartnersHtmlModelWrapper> {

    @Suppress("unused") // needed for kryo
    constructor() : this(null)

    companion object {
        fun byOriginal(original: List<PartnerHtmlModel>) =
            CachedPartnersHtmlModel(partners = original.map { CachedPartnerHtmlModel.byOriginal(it) })
    }

    override fun toModel() =
        PartnersHtmlModelWrapper(partners!!.map { it.toModel() })

}

data class CachedPartnerHtmlModel(
    val id: String?,
    val shortName: String?,
    val name: String?
) {
    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null)

    companion object {
        fun byOriginal(original: PartnerHtmlModel) = CachedPartnerHtmlModel(
            id = original.id,
            shortName = original.shortName,
            name = original.name
        )
    }

    fun toModel() = PartnerHtmlModel(
        id = id!!,
        shortName = shortName!!,
        name = name!!
    )
}

class CachedPartnersHtmlModelSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedPartnersHtmlModel>(
    loader = loader,
    bufferSize = 1024 * 64
) {
    override val objectType = CachedPartnersHtmlModel::class.java
}

class CachedPartnersHtmlModelCopier : Copier<CachedPartnersHtmlModel> {
    override fun copyForRead(obj: CachedPartnersHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedPartnersHtmlModel) = obj.copy()
}
