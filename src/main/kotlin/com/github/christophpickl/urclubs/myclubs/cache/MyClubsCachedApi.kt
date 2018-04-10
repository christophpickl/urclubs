package com.github.christophpickl.urclubs.myclubs.cache

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.CourseFilter
import com.github.christophpickl.urclubs.myclubs.HttpApi
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.UserMycJson
import com.github.christophpickl.urclubs.myclubs.cache.entities.ActivityHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.CoursesHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.PartnerDetailHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.PartnersHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.activitySpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.coursesSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.finishedActivitiesCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.entities.finishedActivitiesSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnerSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnersSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.userCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.entities.userSpec
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.service.QuitListener
import com.github.christophpickl.urclubs.service.QuitManager
import com.google.inject.BindingAnnotation
import org.ehcache.CacheManager
import org.ehcache.config.ResourcePools
import java.io.File
import java.time.format.DateTimeFormatter
import javax.inject.Inject

interface MyClubsCacheManager {
    fun clearCaches()
}

@Retention
@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FUNCTION)
@BindingAnnotation
annotation class CacheFile

class MyClubsCachedApi constructor(
    private val delegate: MyClubsApi,
    quitManager: QuitManager,
    cacheDirectory: File?,
    overrideResourcePools: ResourcePools?
) : MyClubsApi, MyClubsCacheManager, QuitListener {

    @Inject
    constructor(
        @HttpApi delegate: MyClubsApi,
        quitManager: QuitManager,
        @CacheFile cacheDirectory: File

    ) : this(delegate, quitManager, cacheDirectory, null)

    private val log = LOG {}

    private val cacheKeyDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val cacheManager: CacheManager

    init {
        log.info { "Init cache: cacheDirectory=$cacheDirectory, overrideResourcePools=$overrideResourcePools" }
        cacheManager = CacheBuilder.build(cacheDirectory, overrideResourcePools)
        quitManager.addQuitListener(this)
    }

    override fun clearCaches() {
        log.info { "clearCaches()" }

        CacheBuilder.cacheSpecs.forEach {
            log.trace { "Removing cache: $it" }
            cacheManager.lookupCache(it).clear()
        }
    }

    override fun onQuit() {
        log.debug { "onQuit() ... close cache" }
        cacheManager.close()
    }

    override fun loggedUser(): UserMycJson =
        cacheManager.getOrPutKeyCached(delegate, userSpec, userCoordinates)

    override fun partners(): List<PartnerHtmlModel> {
        log.debug { "partners()" }
        return cacheManager.getOrPutKeyCached(delegate, partnersSpec, buildCacheCoordinatesBySuperModel(
            cacheKey = "partnersKey",
            fetchModel = { myclubs -> PartnersHtmlModelWrapper(myclubs.partners()) }
        )).wrapped
    }

    override fun courses(filter: CourseFilter): List<CourseHtmlModel> =
        cacheManager.getOrPutKeyCached(delegate, coursesSpec, buildCacheCoordinatesBySuperModel(
            cacheKey = filter.cacheKey(),
            fetchModel = { myclubs -> CoursesHtmlModelWrapper(myclubs.courses(filter)) }
        )).wrapped

    override fun finishedActivities(): List<FinishedActivityHtmlModel> =
        cacheManager.getOrPutKeyCached(delegate, finishedActivitiesSpec, finishedActivitiesCoordinates)

    override fun partner(shortName: String): PartnerDetailHtmlModel =
        cacheManager.getOrPutKeyCached(delegate, partnerSpec, buildCacheCoordinatesBySuperModel(
            cacheKey = shortName,
            fetchModel = { myclubs -> PartnerDetailHtmlModelWrapper(myclubs.partner(shortName)) }
        )).wrapped

    override fun activity(filter: ActivityFilter): ActivityHtmlModel =
        cacheManager.getOrPutKeyCached(delegate, activitySpec, buildCacheCoordinatesBySuperModel(
            cacheKey = filter.cacheKey(),
            fetchModel = { myclubs -> ActivityHtmlModelWrapper(myclubs.activity(filter)) }
        )).wrapped

    private fun ActivityFilter.cacheKey() = activityId

    private fun CourseFilter.cacheKey() =
        "${start.format(cacheKeyDateTimeFormat)}--${end.format(cacheKeyDateTimeFormat)}"

}
