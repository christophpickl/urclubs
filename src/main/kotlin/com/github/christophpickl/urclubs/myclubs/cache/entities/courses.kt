package com.github.christophpickl.urclubs.myclubs.cache.entities

import com.github.christophpickl.urclubs.myclubs.cache.AbstractCachedSerializer
import com.github.christophpickl.urclubs.myclubs.cache.CacheSpec
import com.github.christophpickl.urclubs.myclubs.cache.ToCacheable
import com.github.christophpickl.urclubs.myclubs.cache.ToModelable
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import org.ehcache.spi.copy.Copier
import java.time.Duration
import java.time.temporal.ChronoUnit

val coursesSpec: CacheSpec<CachedCoursesHtmlModel, CoursesHtmlModelWrapper> = CacheSpec(
    cacheAlias = "coursesAlias",
    valueType = CachedCoursesHtmlModel::class.java,
    duration = Duration.of(4, ChronoUnit.HOURS),
    serializerType = CachedCoursesHtmlModelSerializer::class.java,
    copierType = CachedCoursesHtmlModelCopier::class.java
)

class CachedCoursesHtmlModelSerializer(loader: ClassLoader) : AbstractCachedSerializer<CachedCoursesHtmlModel>(loader) {
    override val objectType = CachedCoursesHtmlModel::class.java
}

class CachedCoursesHtmlModelCopier : Copier<CachedCoursesHtmlModel> {
    override fun copyForRead(obj: CachedCoursesHtmlModel) = obj.copy()
    override fun copyForWrite(obj: CachedCoursesHtmlModel) = obj.copy()
}

data class CoursesHtmlModelWrapper(
    val wrapped: List<CourseHtmlModel>
) : ToCacheable<CachedCoursesHtmlModel> {
    override fun toCache() = CachedCoursesHtmlModel.byOriginal(wrapped)
}

data class CachedCoursesHtmlModel(
    val courses: List<CachedCourseHtmlModel>?
) : ToModelable<CoursesHtmlModelWrapper> {

    @Suppress("unused") // needed for kryo
    constructor() : this(null)

    companion object {
        fun byOriginal(original: List<CourseHtmlModel>) =
            CachedCoursesHtmlModel(courses = original.map { CachedCourseHtmlModel.byOriginal(it) })
    }

    override fun toModel() =
        CoursesHtmlModelWrapper(courses!!.map { it.toModel() })

}


data class CachedCourseHtmlModel(
    val id: String?,
    val time: String?,
    val timestamp: String?,
    val title: String?,
    val partner: String?,
    val category: String?
) {
    @Suppress("unused") // needed for kryo
    constructor() : this(null, null, null, null, null, null)

    companion object {
        fun byOriginal(original: CourseHtmlModel) = CachedCourseHtmlModel(
            id = original.id,
            time = original.time,
            timestamp = original.timestamp,
            title = original.title,
            partner = original.partner,
            category = original.category
        )
    }

    fun toModel() = CourseHtmlModel(
        id = id!!,
        time = time!!,
        timestamp = timestamp!!,
        title = title!!,
        partner = partner!!,
        category = category!!
    )
}
