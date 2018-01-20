package com.github.christophpickl.urclubs.backend

import com.github.christophpickl.urclubs.Partner
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.BasicHttpContext
import org.jsoup.Jsoup

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

class Http {

    private val httpClient = HttpClientBuilder.create().build()

    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    fun execute(request: HttpUriRequest): CloseableHttpResponse {
        return httpClient.execute(request, httpContext)
    }

}
