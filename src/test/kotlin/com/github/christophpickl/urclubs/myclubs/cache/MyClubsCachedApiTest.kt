package com.github.christophpickl.urclubs.myclubs.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.activity.ActivityType
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.Http
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.myclubs.UserMycJson
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.QuitManager
import com.github.christophpickl.urclubs.service.testInstance
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.util.Modules
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.whenever
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.assertj.core.api.Assertions.assertThat
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import org.mockito.Mockito.verify
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File
import java.util.Date
import kotlin.reflect.KFunction1

private val testResourcePool = ResourcePoolsBuilder.newResourcePoolsBuilder()
    .heap(5, MemoryUnit.MB)
    .offheap(50, MemoryUnit.MB)
    .build()

@Test
class MyClubsCachedApiTest {

    private lateinit var delegateApi: MyClubsApi
    private lateinit var cachedApi: MyClubsCachedApi
    private val anyUser = UserMycJson.testInstance()

    @BeforeMethod
    fun initState() {
        delegateApi = mock()
        cachedApi = MyClubsCachedApi(
            delegate = delegateApi,
            quitManager = QuitManager(),
            overrideResourcePools = testResourcePool,
            cacheDirectory = null
        )
    }

    @AfterMethod
    fun tearDown() {
        cachedApi.onQuit()
    }

    fun `loggedUser - two requests should request delegate 1 time`() {
        assertCacheStores(UserMycJson.testInstance(), MyClubsApi::loggedUser)
    }

    fun `partners - two requests should request delegate 1 time`() {
        assertCacheStores(listOf(PartnerHtmlModel.testInstance()), MyClubsApi::partners)
    }

    fun `finishedActivities - two requests should request delegate 1 time`() {
        assertCacheStores(listOf(FinishedActivityHtmlModel.testInstance()), MyClubsApi::finishedActivities)
    }

    private fun activityFilterWithCacheKey(key: String) = ActivityFilter(activityId = key, timestamp = "", type = ActivityType.Course)

    fun `activity - two requests with same key should request delegate 1 time`() {
        val filter = activityFilterWithCacheKey("filter")
        val given = ActivityHtmlModel.testInstance()
        whenever(delegateApi.activity(filter)).thenReturn(given)

        val result1 = cachedApi.activity(filter)
        val result2 = cachedApi.activity(filter)

        verify(delegateApi, times(1)).activity(filter)
        assertThat(given).isEqualTo(result1).isEqualTo(result2)
    }

    fun `activity - two requests with different key should request delegate 2 times `() {
        val filter1 = activityFilterWithCacheKey("filter1")
        val filter2 = activityFilterWithCacheKey("filter2")
        val given1 = ActivityHtmlModel.testInstance().copy(partnerShortName = "partner1")
        val given2 = ActivityHtmlModel.testInstance().copy(partnerShortName = "partner2")
        whenever(delegateApi.activity(filter1)).thenReturn(given1)
        whenever(delegateApi.activity(filter2)).thenReturn(given2)

        val result1 = cachedApi.activity(filter1)
        val result2 = cachedApi.activity(filter2)

        verify(delegateApi, times(1)).activity(filter1)
        verify(delegateApi, times(1)).activity(filter2)
        assertThat(given1).isEqualTo(result1)
        assertThat(given2).isEqualTo(result2)
    }

    fun `partner - two requests with same key should request delegate 1 time`() {
        val shortName = "shortName"
        val given = PartnerDetailHtmlModel.testInstance()
        whenever(delegateApi.partner(shortName)).thenReturn(given)

        val result1 = cachedApi.partner(shortName)
        val result2 = cachedApi.partner(shortName)

        verify(delegateApi, times(1)).partner(shortName)
        assertThat(given).isEqualTo(result1).isEqualTo(result2)
    }

    fun `partner - two requests with different key should request delegate 2 times `() {
        val shortName1 = "shortName1"
        val shortName2 = "shortName2"
        val given1 = PartnerDetailHtmlModel.testInstance().copy(name = "name1")
        val given2 = PartnerDetailHtmlModel.testInstance().copy(name = "name2")
        whenever(delegateApi.partner(shortName1)).thenReturn(given1)
        whenever(delegateApi.partner(shortName2)).thenReturn(given2)

        val result1 = cachedApi.partner(shortName1)
        val result2 = cachedApi.partner(shortName2)

        verify(delegateApi, times(1)).partner(shortName1)
        verify(delegateApi, times(1)).partner(shortName2)
        assertThat(given1).isEqualTo(result1)
        assertThat(given2).isEqualTo(result2)
    }

    private fun <G> assertCacheStores(given: G, cachedMethod: KFunction1<MyClubsApi, G>) {
        whenever(cachedMethod(delegateApi)).thenReturn(given)

        val result1 = cachedMethod(cachedApi)
        val result2 = cachedMethod(cachedApi)

        cachedMethod(verify(delegateApi, times(1)))
        assertThat(given).isEqualTo(result1).isEqualTo(result2)
    }

    fun `clearCaches - two requests and clear cache in between should again return from delegate`() {
        whenever(delegateApi.loggedUser()).thenReturn(anyUser)

        cachedApi.loggedUser()
        cachedApi.clearCaches()
        cachedApi.loggedUser()

        verify(delegateApi, times(2)).loggedUser()
    }

}

@Test(groups = ["guice"])
class MyClubsCachedApiGuiceTest {

    private val mapper = jacksonObjectMapper()

    fun `When load partners two times Then only one HTTP call was made for login and one for partners`() {
        val http = mock<Http>()
        val loginResponse = mockResponse("success")
        val userResponse = mockResponse(UserMycJson.testInstance())
        whenever(http.execute(any())).thenReturn(loginResponse, userResponse)
        val guice = Guice.createInjector(Modules.override(MyclubsModule()).with(TestModule(http)))
        val myclubs = guice.getInstance(MyClubsApi::class.java)
        guice.getInstance(MyClubsCacheManager::class.java).clearCaches()

        myclubs.partners()
        myclubs.partners()

        verify(http, times(2)).execute(any())
    }

    private fun mockResponse(body: Any, contentType: ContentType? = null) = mock<CloseableHttpResponse>().apply {
        val stringBody = body as? String ?: mapper.writeValueAsString(body)
        val inferredContentType = contentType
            ?: if (body is String) ContentType.TEXT_PLAIN else ContentType.APPLICATION_JSON
        whenever(entity).thenReturn(StringEntity(stringBody, inferredContentType))
    }


    private class TestModule(
        private val httpMock: Http
    ) : AbstractModule() {

        private val log = LOG {}

        override fun configure() {
            bind(Http::class.java).toInstance(httpMock)
            bind(Credentials::class.java).toInstance(Credentials.testInstance())
        }

        @Provides
        @Singleton
        @CacheFile
        fun provideCacheDirectory() = File(System.getProperty("java.io.tmpdir"), randomName()).apply {
            log.debug { "Using test cache directory at: ${this.canonicalPath}" }
        }

        private fun randomName() = "cache_test_dir-${Date().time}"
    }

}
