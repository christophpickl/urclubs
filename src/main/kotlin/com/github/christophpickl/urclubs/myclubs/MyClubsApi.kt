package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.HtmlParser
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.service.Credentials
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.message.BasicNameValuePair
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.util.EntityUtils
import javax.inject.Inject


interface MyClubsApi {

    fun loggedUser(): UserMycJson

    fun partners(): List<PartnerHtmlModel>

    // Requests: https://www.myclubs.com/at/de/partner/
    fun partner(shortName: String): PartnerDetailHtmlModel

    fun courses(filter: CourseFilter): List<CourseHtmlModel>

    //    fun infrastructure(): List<InfrastructureMyc>

    // Requests: https://www.myclubs.com/at/de/partner/sporthalle-wien
    //fun singleActivity() ...

    fun activity(filter: ActivityFilter): ActivityHtmlModel

    fun finishedActivities(): List<FinishedActivityHtmlModel>

}

class MyClubsHttpApi @Inject constructor(
    private val credentials: Credentials,
    private val util: MyclubsUtil
) : MyClubsApi {

    private val log = LOG {}

    private val baseUrl = "https://www.myclubs.com" // TODO inject, in order to make it fakeable/testable (integration tests with wiremock)
    private val baseApiUrl = "$baseUrl/api"
    private val http = Http()
    private val jackson = jacksonObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
    private val parser = HtmlParser()
    private var loggedIn = false

    override fun loggedUser(): UserMycJson {
        log.info { "loggedUser()" }
        loginIfNecessary()

        val response = http.execute(HttpPost("$baseApiUrl/getLoggedUser"))
        val body = response.body
        if (body == "0") {
            log.warn { response.toString() }
            log.warn(body)
            throw Exception("Invalid response!")
        }
        return jackson.readValue(body)
    }

    override fun partners(): List<PartnerHtmlModel> {
        log.info { "partners()" }
        loginIfNecessary()

        val response = http.execute(HttpPost("$baseApiUrl/activities-get-partners").apply {
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

    override fun courses(filter: CourseFilter): List<CourseHtmlModel> {
        log.info { "courses(filter=$filter)" }
        loginIfNecessary()

        val response = http.execute(HttpPost("$baseApiUrl/activities-list-response").apply {
            entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("filters", jackson.writeValueAsString(filter.toFilterMycJson())),
                BasicNameValuePair("country", "at"),
                BasicNameValuePair("language", "de")
            ))
        })
        val json = jackson.readValue<ActivitiesMycJson>(response.body)
        val courses = parser.parseCourses(json.coursesHtml)
        log.trace { "Found ${courses.size} courses." }
        return courses
    }

    // MINOR if would remove the timestamp filter, we could cache the response, right?!
    override fun activity(filter: ActivityFilter): ActivityHtmlModel {
        log.info { "activity(filter=$filter)" }
        loginIfNecessary()

        val response = http.execute(HttpPost("$baseApiUrl/activityDetail").apply {
            entity = UrlEncodedFormEntity(listOf(
                BasicNameValuePair("activityData", filter.activityId),
                BasicNameValuePair("type", filter.type.toActivityTypeMyc().json),
                BasicNameValuePair("date", filter.timestamp),
                BasicNameValuePair("country", "at"),
                BasicNameValuePair("language", "de")
            ))
        })

        // TODO test for not found activity
        return parser.parseActivity(response.body)
    }

    override fun finishedActivities(): List<FinishedActivityHtmlModel> {
        log.debug { "finishedActivities()" }
        loginIfNecessary()

        val response = http.execute(HttpGet("$baseUrl/at/de/profile"))
        return parser.parseProfile(response.body).finishedActivities
    }

    override fun partner(shortName: String): PartnerDetailHtmlModel {
        log.debug { "partner(shortName=$shortName)" }
        loginIfNecessary()

        val response = http.execute(HttpGet(util.createMyclubsPartnerUrl(shortName)))
        val responseBody = response.body
        val partner = try {
            parser.parsePartner(responseBody)
        } catch (e: Exception) {
            log.error { "Failed to parse response body:\n${responseBody}" }
            throw e
        }
        log.trace { "Found: $partner" }
        return partner
    }

    private val CloseableHttpResponse.body: String get() = EntityUtils.toString(entity).trim()


    private fun loginIfNecessary() {
        if (!loggedIn) {
            login()
        }
    }

    private fun login() {
        log.info { "login() as user: ${credentials.email}" }
        if (loggedIn) {
            throw IllegalStateException("already logged in!")
        }

        val response = http.execute(HttpPost("$baseApiUrl/login").apply {
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
        loggedIn = true
    }

}

class MyclubsUtil {
    private val baseUrl = "https://www.myclubs.com" // TODO inject, in order to make it fakeable/testable (integration tests with wiremock)

    fun createMyclubsPartnerUrl(shortName: String) = "$baseUrl/at/de/partner/$shortName"
}

private class Http {

    private val log = LOG {}
    private val httpClient = HttpClientBuilder.create().build()

    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        log.info { "execute(request.uri=${request.uri})" }
        return httpClient.execute(request, httpContext)
    }

}
