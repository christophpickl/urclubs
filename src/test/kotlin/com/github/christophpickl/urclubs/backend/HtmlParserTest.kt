package com.github.christophpickl.urclubs.backend

import com.github.christophpickl.urclubs.Partner
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class HtmlParserTest {

    fun `parsePartners - read valid entry`() {
        assertThat(parse("""<option value="myId" data-slug="ignored-slug">my title</option>"""))
                .containsExactly(Partner(
                        id = "myId",
                        title = "my title"
                ))
    }

    fun `parsePartners - skip all partners entry`() {
        assertThat(parse("""<option value="">Alle Partner</option>"""))
                .isEmpty()
    }

    fun `parsePartners - integration test`() {
        assertThat(parse(readResponse("activities-get-partners.html")))
                .hasSize(174)
    }

    private fun parse(html: String) =
            HtmlParser().parsePartners(html)

    private fun readResponse(fileName: String) =
            javaClass.getResource("/urclubs/responses/$fileName").readText()

}
