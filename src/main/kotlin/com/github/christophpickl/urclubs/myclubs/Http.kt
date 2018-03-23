package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.BasicHttpContext

interface Http {
    fun execute(request: HttpUriRequest): CloseableHttpResponse
}

class HttpImpl : Http {

    private val log = LOG {}
    private val httpClient = HttpClientBuilder.create().build()

    private val httpContext = BasicHttpContext().apply {
        setAttribute(HttpClientContext.COOKIE_STORE, BasicCookieStore())
    }

    override fun execute(request: HttpUriRequest): CloseableHttpResponse {
        log.info { "execute(request.uri=${request.uri})" }
        return httpClient.execute(request, httpContext)
    }

}
