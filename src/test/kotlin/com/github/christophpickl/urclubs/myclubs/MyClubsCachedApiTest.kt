package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.testInstance
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Scopes
import com.google.inject.util.Modules
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.whenever
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.entity.ContentType
import org.apache.http.entity.StringEntity
import org.mockito.Mockito.verify
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import javax.inject.Inject

@Test
class MyClubsCachedApiTest {

    private lateinit var delegateApi: MyClubsApi
    private lateinit var cachedApi: MyClubsCachedApi

    @BeforeMethod
    fun initState() {
        delegateApi = mock()
        cachedApi = MyClubsCachedApi(delegateApi)
    }

    fun `foo`() {
        val result = cachedApi.loggedUser()
        // FIXME implement me
    }

}

@Test(groups = ["guice"])
class MyClubsCachedApiGuiceTest {

    private class TestModule(
            private val httpMock: Http
    ) : AbstractModule() {
        override fun configure() {
            bind(Http::class.java).toInstance(httpMock)
            bind(Credentials::class.java).toInstance(Credentials.testInstance())
        }
    }

    private val mapper = jacksonObjectMapper()

    fun `When get loggedUser two times Then only one HTTP call was made`() {
        val http = mock<Http>()
        whenever(http.execute(any())).thenReturn(mockResponse("success"), mockResponse(UserMycJson.testInstance()))
        val guice = Guice.createInjector(Modules.override(MyclubsModule()).with(TestModule(http)))
        val myclubs = guice.getInstance(MyClubsApi::class.java)

        myclubs.loggedUser()
        myclubs.loggedUser()

        verify(http, times(2)).execute(any())
    }

    private fun mockResponse(body: Any, contentType: ContentType? = null) = mock<CloseableHttpResponse>().apply {
        val stringBody = if (body is String) {
            body
        } else {
            mapper.writeValueAsString(body)
        }
        val inferredContentType = if (contentType != null) {
            contentType
        } else {
            if (body is String) {
                ContentType.TEXT_PLAIN
            } else {
                ContentType.APPLICATION_JSON
            }
        }
        whenever(entity).thenReturn(StringEntity(stringBody, inferredContentType))
    }

}
