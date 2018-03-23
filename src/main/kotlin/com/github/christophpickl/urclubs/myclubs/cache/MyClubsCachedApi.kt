package com.github.christophpickl.urclubs.myclubs.cache

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.HttpApi
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.UserMycJson
import com.github.christophpickl.urclubs.myclubs.cache.entities.ActivityHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.PartnerDetailHtmlModelWrapper
import com.github.christophpickl.urclubs.myclubs.cache.entities.activitySpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.finishedActivitiesSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.finishedActivitiesSpecCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnerSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnersSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.partnersSpecCoordinates
import com.github.christophpickl.urclubs.myclubs.cache.entities.userSpec
import com.github.christophpickl.urclubs.myclubs.cache.entities.userSpecCoordinates
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.service.QuitListener
import com.github.christophpickl.urclubs.service.QuitManager
import com.google.inject.BindingAnnotation
import org.ehcache.CacheManager
import org.ehcache.config.ResourcePools
import java.io.File
import javax.inject.Inject

interface MyClubsCacheManager : QuitListener {
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
) : MyClubsApi, MyClubsCacheManager {
    @Inject
    constructor(
        @HttpApi delegate: MyClubsApi,
        quitManager: QuitManager,
        @CacheFile cacheDirectory: File

    ) : this(delegate, quitManager, cacheDirectory, null)

    private val log = LOG {}

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
            cacheManager.getFor(it).clear()
        }
    }

    override fun onQuit() {
        log.debug { "onQuit close cache" }
        cacheManager.close()
    }

    override fun loggedUser(): UserMycJson =
        cacheManager.getOrPutSingledCache(delegate, userSpec, userSpecCoordinates)

    override fun partners(): List<PartnerHtmlModel> =
        cacheManager.getOrPutSingledCache(delegate, partnersSpec, partnersSpecCoordinates)

    override fun finishedActivities(): List<FinishedActivityHtmlModel> =
        cacheManager.getOrPutSingledCache(delegate, finishedActivitiesSpec, finishedActivitiesSpecCoordinates)

    override fun partner(shortName: String): PartnerDetailHtmlModel =
        cacheManager.getOrPutKeyCached(delegate, partnerSpec, keyedCoordinates(
            cacheKey = shortName,
            request = shortName,
            withDelegate = { myclubs -> PartnerDetailHtmlModelWrapper(myclubs.partner(shortName)) }
        )).wrapped

    override fun activity(filter: ActivityFilter): ActivityHtmlModel =
        cacheManager.getOrPutKeyCached(delegate, activitySpec, keyedCoordinates(
            cacheKey = filter.cacheKey(),
            request = filter,
            withDelegate = { myclubs -> ActivityHtmlModelWrapper(myclubs.activity(filter)) }
        )).wrapped

    private fun ActivityFilter.cacheKey() = activityId // MINOR should be enough, right?!

}
