package com.github.christophpickl.urclubs

import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.util.EntityUtils


/*
https://www.myclubs.com/api/activities-get-partners
https://www.myclubs.com/api/categories-response
https://www.myclubs.com/api/cities-response
 */

object UrClubs {

    @JvmStatic
    fun main(args: Array<String>) {
        val email = args[0]
        val password = args[1]

        val myclubs = MyClubsApi(email, password)
        myclubs.login()
        myclubs.loggedUser()
    }

}

class MyClubsApi(
        private val email: String,
        private val password: String
) {

    private val baseUrl = "https://www.myclubs.com/api"
    private val session = Session()

    fun login() {
        println("login()")

        val response = session.execute(HttpPost("$baseUrl/login").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("email", email),
                    BasicNameValuePair("password", password),
                    BasicNameValuePair("staylogged", "true")
            ))
        })

        val responseString = EntityUtils.toString(response.entity).trim()
        if (responseString != "success") {
            println("Response: $response")
            println("Entity:")
            println(responseString)
            throw Exception("Login failed!")
        }
    }

    fun loggedUser() {
        println("loggedUser()")
        val response = session.execute(HttpPost("$baseUrl/getLoggedUser"))
        val responseString = EntityUtils.toString(response.entity)
        println(response)
        println(responseString)
        //  {"user_id":"dtEkYdhGIF","email":"christoph.pickl@gmail.com","firstname":"Christoph","lastname":"Pickl"}
        // return User()
    }
}

data class User(
        val id: String,
        val email: String,
        val firstName: String,
        val lastName: String
)

class Session {
    private val httpClient = HttpClientBuilder.create().build()
    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        return httpClient.execute(request, httpContext)
    }


}
