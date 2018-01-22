package com.github.christophpickl.urclubs.backend

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.Credentials
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
import javax.inject.Inject

interface MyClubsApi{
    fun login()
    fun loggedUser(): UserMycJson
    fun partners(): List<PartnerMyc>
    fun activities(): List<ActivityMyc>
}

class MyClubsHttpApi @Inject constructor(
        private val credentials: Credentials
): MyClubsApi {
    private val log = LOG {}
    private val baseUrl = "https://www.myclubs.com/api"
    private val http = Http()
    private val jackson = jacksonObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
    private val parser = HtmlParser()

    override fun login() {
        log.info("login()")

        val response = http.execute(HttpPost("$baseUrl/login").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("email", credentials.email),
                    BasicNameValuePair("password", credentials.password),
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

    override fun loggedUser(): UserMycJson {
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

    override fun partners(): List<PartnerMyc> {
        log.info("partners()")
        val response = http.execute(HttpPost("$baseUrl/activities-get-partners").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("country", "at"),
                    BasicNameValuePair("city", "wien"),
                    BasicNameValuePair("language", "de")
            ))
        })
        val partners = parser.parsePartners(response.body)
        log.trace { "Found ${partners.size} partners." }
        return partners
    }

    override fun activities(): List<ActivityMyc> {
        log.info("activities()")
        val filter = FilterMycJson(
                date = listOf("21.01.2018"),
                time = listOf("09:00", "15:00")
        )
        val response = http.execute(HttpPost("$baseUrl/activities-list-response").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("filters", jackson.writeValueAsString(filter)),
                    BasicNameValuePair("country", "at"),
                    BasicNameValuePair("language", "de")
            ))
        })

        val json = jackson.readValue<ActivitiesMycJson>(response.body)
        val courses = parser.parseActivities(json.coursesHtml)
        val infrastructure = parser.parseActivities(json.infrastructuresHtml)
        log.trace { "Found ${courses.size} courses and ${infrastructure.size} infrastructure activities." }
        return mutableListOf<ActivityMyc>().apply {
            addAll(courses)
            addAll(infrastructure)
        }
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
