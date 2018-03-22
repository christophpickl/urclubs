package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import org.ehcache.spi.copy.Copier

data class CachedPartnerHtmlModel(
    val id: String?,
    val shortName: String?,
    val name: String?
) {

    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null)

    constructor(original: PartnerHtmlModel) : this(
        id = original.id,
        shortName = original.shortName,
        name = original.name
    )

    fun toPartnerHtmlModel() = PartnerHtmlModel(
        id = id!!,
        shortName = shortName!!,
        name = name!!
    )
}

class CachedPartnerHtmlModelSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedPartnerHtmlModel>(loader) {
    override val objectType = CachedPartnerHtmlModel::class.java
}

class CachedPartnerHtmlModelCopier : Copier<CachedPartnerHtmlModel> {
    override fun copyForRead(obj: CachedPartnerHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedPartnerHtmlModel) = obj.copy()
}
