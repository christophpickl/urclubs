package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.service.Credentials
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

interface MyClubsApi {
    fun login()
    fun loggedUser(): UserMycJson
    fun partners(): List<PartnerMyc>
    fun courses(filter: CourseFilter): List<CourseMyc>
//    fun infrastructure(): List<InfrastructureMyc>
    fun activity(filter: ActivityFilter): ActivityMyc
}

class MyClubsHttpApi @Inject constructor(
        private val credentials: Credentials
) : MyClubsApi {

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

    override fun courses(filter: CourseFilter): List<CourseMyc> {
        log.info("courses(filter=$filter)")
        val response = http.execute(HttpPost("$baseUrl/activities-list-response").apply {
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
    override fun activity(filter: ActivityFilter): ActivityMyc {
        log.info("activity(filter=$filter)")
        val response = http.execute(HttpPost("$baseUrl/activityDetail").apply {
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
