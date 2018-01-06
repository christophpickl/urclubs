package com.github.christophpickl.urclubs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.kpotpourri.common.logging.LOG
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

class MyClubsApi(
        private val email: String,
        private val password: String
) {
    private val log = LOG {}
    private val baseUrl = "https://www.myclubs.com/api"
    private val http = Http()
    private val jackson = jacksonObjectMapper()

    fun login() {
        log.info("login()")

        val response = http.execute(HttpPost("$baseUrl/login").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("email", email),
                    BasicNameValuePair("password", password),
                    BasicNameValuePair("staylogged", "true")
            ))
        })

        val body = response.body
        if (body != "success") {
            log.warn { response.toString() }
            log.warn(body)
            throw Exception("Login failed!")
        }
    }

    fun loggedUser(): User {
        log.info("loggedUser()")
        val response = http.execute(HttpPost("$baseUrl/getLoggedUser"))
        val body = response.body
        if (body == "0") {
            log.warn { response.toString() }
            log.warn(body)
            throw Exception("Invalid response!")
        }
        return jackson.readValue(body)
    }

    fun foo() {
        log.info("foo()")
        val response = http.execute(HttpPost("$baseUrl/categories-response").apply {
            addHeader("accept", "application/json")
        })
        val body = response.body
        println(response)
        println(body)
    }

    fun partners() {
        /*
        Request URL:https://www.myclubs.com/api/activities-get-partners

        city:niederoesterreich
        language:de
        country:at



        activities-list-response
filters:{"categories":[],
"date":["07.01.2018"],"time":["05:00","23:00"],
"favourite":"false","city":"niederoesterreich",
"partner":"","type":["infrastructure","course"]}
country:at
language:de

https://www.myclubs.com/api/categories-response
    country:at
    language:de
         */
    }

    private val CloseableHttpResponse.body: String get() = EntityUtils.toString(entity).trim()

}

private class Http {

    private val httpClient = HttpClientBuilder.create().build()
    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        return httpClient.execute(request, httpContext)
    }

}
