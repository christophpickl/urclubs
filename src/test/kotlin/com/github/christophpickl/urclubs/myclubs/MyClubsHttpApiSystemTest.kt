package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.PropertiesFileCredentialsProvider
import org.testng.annotations.Test

@Test(groups = ["system"])
class MyClubsHttpApiSystemTest {

    private val validCredentials by lazy { PropertiesFileCredentialsProvider().get() }
    private val invalidCredentials = Credentials(email = "not@user.com", password = "secret")

    fun `Given valid credentials When login Then succeed`() {
        val myclubs = myclubs(validCredentials)

        myclubs.loggedUser()
    }

    @Test(expectedExceptions = [LoginException::class])
    fun `Given invalid credentials When login Then fail`() {
        val myclubs = myclubs(invalidCredentials)

        myclubs.loggedUser()
    }

    private fun myclubs(credentials: Credentials) = MyClubsHttpApi(credentials, MyclubsUtil(), HttpImpl())

}
