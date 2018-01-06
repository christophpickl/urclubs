package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
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
        configureLogging()
        val email = args[0]
        val password = args[1]

        val myclubs = MyClubsApi(email, password)
//        myclubs.login()
        myclubs.loggedUser()
    }

    private fun configureLogging() {
        Logback4k.reconfigure {
            rootLevel = Level.ALL
            packageLevel(Level.WARN, "org.apache.http")
            addConsoleAppender {
                pattern = "[%-5level] %logger{60} - %msg%n"
            }
        }
    }

}

class MyClubsApi(
        private val email: String,
        private val password: String
) {
    private val log = LOG {}
    private val baseUrl = "https://www.myclubs.com/api"
    private val session = Session()
    private val jackson = jacksonObjectMapper()

    fun login() {
        log.info("login()")

        val response = session.execute(HttpPost("$baseUrl/login").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("email", email),
                    BasicNameValuePair("password", password),
                    BasicNameValuePair("staylogged", "true")
            ))
        })

        val responseString = EntityUtils.toString(response.entity).trim()
        if (responseString != "success") {
            log.warn { response.toString() }
            log.warn(responseString)
            throw Exception("Login failed!")
        }
    }

    fun loggedUser() {
        log.info("loggedUser()")
        val response = session.execute(HttpPost("$baseUrl/getLoggedUser"))
        val responseString = EntityUtils.toString(response.entity).trim()
        if (responseString == "0") {
            log.warn { response.toString() }
            log.warn(responseString)
            throw Exception("Invalid response!")
        }
        // return User()
    }
}

data class User(
        @JsonProperty("user_id")
        val id: String,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("firstname")
        val firstName: String,
        @JsonProperty("lastname")
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
