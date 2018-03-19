package com.github.christophpickl.urclubs.myclubs

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.util.Modules
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import org.mockito.Mockito
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

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
    private class TestModule(private val httpMock: Http) : AbstractModule() {
        override fun configure() {
            bind(Http::class.java).toInstance(httpMock)
        }
    }

    fun `When get loggedUser two times Then only one HTTP call was made`() {
        val http = mock<Http>()
        val guice = Guice.createInjector(Modules.override(MyclubsModule()).with(TestModule(http)))
        val myclubs = guice.getInstance(MyClubsApi::class.java)

        myclubs.loggedUser()
        myclubs.loggedUser()


        Mockito.verify(http, times(1)).execute(any())
    }
}
