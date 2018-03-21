package com.github.christophpickl.urclubs.myclubs.cache

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.Http
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.myclubs.UserMycJson
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.github.christophpickl.urclubs.service.Credentials
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

private val testResourcePool = ResourcePoolsBuilder.newResourcePoolsBuilder()
        .heap(5, MemoryUnit.MB)
        .offheap(50, MemoryUnit.MB)
        .build()

@Test
class MyClubsCachedApiTest {

    private lateinit var delegateApi: MyClubsApi
    private lateinit var cachedApi: MyClubsCachedApi
    private val userMycJson = UserMycJson.testInstance()

    @BeforeMethod
    fun initState() {
        delegateApi = mock()
        cachedApi = MyClubsCachedApi(
                delegate = delegateApi,
                overrideResourcePools = testResourcePool,
                cacheDirectory = null
        )
    }

    @AfterMethod
    fun tearDown() {
        cachedApi.closeCache()
    }

    fun `loggedUser - single request should load and return from delegate`() {
        whenever(delegateApi.loggedUser()).thenReturn(userMycJson)

        val result = cachedApi.loggedUser()

        verify(delegateApi, times(1)).loggedUser()
        assertThat(result).isEqualTo(userMycJson)
    }

    fun `loggedUser - two requests should load 2nd from cache`() {
        whenever(delegateApi.loggedUser()).thenReturn(userMycJson)

        cachedApi.loggedUser()
        cachedApi.loggedUser()

        verify(delegateApi, times(1)).loggedUser()
    }

    fun `clearCaches - two requests and clear cache in between should again return from delegate`() {
        whenever(delegateApi.loggedUser()).thenReturn(userMycJson)
        cachedApi.loggedUser()

        cachedApi.clearCaches()
        cachedApi.loggedUser()

        verify(delegateApi, times(2)).loggedUser()
    }

}

@Test(groups = ["guice"])
class MyClubsCachedApiGuiceTest {

    private val mapper = jacksonObjectMapper()

    fun `When get loggedUser two times Then only one HTTP call was made for login and one for loggedUser`() {
        val http = mock<Http>()
        val loginResponse = mockResponse("success")
        val userResponse = mockResponse(UserMycJson.testInstance())
        whenever(http.execute(any())).thenReturn(loginResponse, userResponse)
        val guice = Guice.createInjector(Modules.override(MyclubsModule()).with(TestModule(http)))
        val myclubs = guice.getInstance(MyClubsApi::class.java)
        guice.getInstance(MyClubsCacheManager::class.java).clearCaches()

        myclubs.loggedUser()
        myclubs.loggedUser()

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
