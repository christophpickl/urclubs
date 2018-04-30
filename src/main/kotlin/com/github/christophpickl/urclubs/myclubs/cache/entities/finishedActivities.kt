package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

val finishedActivitiesSpec: CacheSpec<CachedFinishedActivitiesHtmlModel> = CacheSpec(
    cacheAlias = "finishedActivitesAlias",
    valueType = CachedFinishedActivitiesHtmlModel::class.java,
    duration = Duration.of(4, ChronoUnit.HOURS),
    serializerType = CachedFinishedActivitiesHtmlModelSerializer::class.java,
    copierType = CachedFinishedActivitiesHtmlModelCopier::class.java
)
val finishedActivitiesCoordinates = CacheCoordinates(
    cacheKey = "finishedActivitesKey",
    toModelTransformer = { it.toModel() },
    fetchModel = { it.finishedActivities() },
    toCachedTransformer = { CachedFinishedActivitiesHtmlModel.byOriginal(it) }
)

data class CachedFinishedActivitiesHtmlModel(
    val partners: List<CachedFinishedActivitiyHtmlModel>?
) {
    @Suppress("unused") // needed for kryo
    constructor() : this(null)

    companion object {
        fun byOriginal(original: List<FinishedActivityHtmlModel>) =
            CachedFinishedActivitiesHtmlModel(partners = original.map { CachedFinishedActivitiyHtmlModel.byOriginal(it) })
    }

    fun toModel() = partners!!.map { it.toModel() }

}

data class CachedFinishedActivitiyHtmlModel(
    val date: LocalDateTime?,
    val category: String?,
    val title: String?,
    val locationHtml: String?
) {
    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null, null)

    companion object {
        fun byOriginal(original: FinishedActivityHtmlModel) = CachedFinishedActivitiyHtmlModel(
            date = original.date,
            category = original.category,
            title = original.title,
            locationHtml = original.locationHtml
        )
    }

    fun toModel() = FinishedActivityHtmlModel(
        date = date!!,
        category = category!!,
        title = title!!,
        locationHtml = locationHtml!!
    )
}

class CachedFinishedActivitiesHtmlModelSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedFinishedActivitiesHtmlModel>(loader) {
    override val objectType = CachedFinishedActivitiesHtmlModel::class.java
}

class CachedFinishedActivitiesHtmlModelCopier : Copier<CachedFinishedActivitiesHtmlModel> {
    override fun copyForRead(obj: CachedFinishedActivitiesHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedFinishedActivitiesHtmlModel) = obj.copy()
}
