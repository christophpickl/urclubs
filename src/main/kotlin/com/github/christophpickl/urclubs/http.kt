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
import org.jsoup.Jsoup

class MyClubsApi(
        private val credentials: Credentials
) {
    private val log = LOG {}
    private val baseUrl = "https://www.myclubs.com/api"
    private val http = Http()
    private val jackson = jacksonObjectMapper()
    private val parser = HtmlParser()

    fun login() {
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

    fun partners(): List<Partner> {
        log.info("partners()")
        val response = http.execute(HttpPost("$baseUrl/activities-get-partners").apply {
            entity = UrlEncodedFormEntity(listOf(
                    BasicNameValuePair("country", "at"),
                    BasicNameValuePair("city", "wien"),
                    BasicNameValuePair("language", "de")
            ))
        })
        return parser.parsePartners(response.body)

    }

    private val CloseableHttpResponse.body: String get() = EntityUtils.toString(entity).trim()

}

class HtmlParser {

    fun parsePartners(html: String) =
            Jsoup.parse(html).select("option").mapNotNull {
                val id = it.attr("value")
                if (id == "") return@mapNotNull null
                Partner(
                        id = id,
                        title = it.text()
                )
            }

}

data class Partner(
        val id: String,
        val title: String
)

private class Http {

    private val httpClient = HttpClientBuilder.create().build()
    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        return httpClient.execute(request, httpContext)
    }

}
