package com.github.christophpickl.urclubs.backend

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.Credentials
import com.github.christophpickl.urclubs.Partner
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.message.BasicNameValuePair
import org.apache.http.util.EntityUtils

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
