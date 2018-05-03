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
import com.google.inject.BindingAnnotation
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils
import javax.inject.Inject
import kotlin.annotation.AnnotationRetention.RUNTIME

@Retention(RUNTIME)
@Target(AnnotationTarget.VALUE_PARAMETER)
@BindingAnnotation
annotation class HttpApi

class MyClubsHttpApi @Inject constructor(
        private val credentials: Credentials,
        private val util: MyclubsUtil,
        private val http: Http
) : MyClubsApi {

    private val log = LOG {}
    private val baseUrl = "https://www.myclubs.com"
    private val baseApiUrl = "$baseUrl/api"
    private val filterCountry = "at"
    private val filterLanguage = "de"
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
                BasicNameValuePair("country", filterCountry),
                BasicNameValuePair("language", filterLanguage)
            ))
        })
        val json = jackson.readValue<ActivitiesMycJson>(response.body)
        val courses = parser.parseCourses(json.coursesHtml)
        log.trace { "Found ${courses.size} courses." }
        return courses
    }

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

        val loginResponse = parseLoginResponse(response.body)
        if (loginResponse.state != "success") {
            log.warn { "Response:\n$response" }
            log.warn { "Response body:\n${response.body}" }
            throw LoginException("Login failed (state: ${loginResponse.state})!")
        }
        loggedIn = true

    }

    private fun parseLoginResponse(response: String): LoginResponse {
        if (response == "fail") {
            return LoginResponse.Failed
        }
        val parts = response.split("|||")
        if (parts.size != 4) {
            throw IllegalArgumentException("Expected response to contain 4 elements separated by |||. But was: [$response]")
        }
        // success|||dtEkYdhGIF|||79|||UNLIMITED
        return LoginResponse(
                state = parts[0],
                id = parts[1],
                number = parts[2].toInt(),
                productType= parts[3]
        )
    }
}

class LoginException(message: String) : Exception(message)

private data class LoginResponse(
        val state: String, // success
        val id: String, // dtEkYdhGIF
        val number: Int, // 79
        val productType: String // UNLIMITED
) {
    companion object {
        val Failed = LoginResponse(state = "fail", id = "", number = -1, productType = "")
    }
}
