package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class HtmlParserTest {

    private val jackson = jacksonObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun `parsePartners - read valid entry`() {
        assertThatSingleElement(HtmlParser().parsePartners("""<option value="myId" data-slug="short-name">my name</option>"""),
                PartnerMyc(
                        id = "myId",
                        name = "my name",
                        shortName = "short-name"
                ))
    }

    fun `parsePartners - skip all partners entry`() {
        assertThat(HtmlParser().parsePartners("""<option value="">Alle Partner</option>"""))
                .isEmpty()
    }

    fun `parsePartners - integration test`() {
        assertThat(HtmlParser().parsePartners(readResponse("activities-get-partners.html")))
                .hasSize(174)
    }

    fun `parseCourses - simple`() {
        val json = ActivitiesMycJson(coursesHtml = """
            <div class="activities__list__content  js-activities-list-content">
                <ul>
                    <li class="js-activities-item  " data-type="course" data-activity="SEdFOCOPkF" data-location="23" data-date="1516446000">
                     <span class="text">
                        <table cellpadding="0" cellspacing="0" border="0">
                           <tr>
                              <td class="time">16:00</td>
                              <td>
                                 <span class="cat">Fitnesskurs</span>
                                 <h3>Doshinkan Karatedo</h3>
                                 <p><span class="text__partner">City & Country Club Wienerberg, 1100 Wien</span></p>
                              </td>
                           </tr>
                        </table>
                     </span>
                    </li>
                </ul>
                <div class="activities__list__no-results" id="no-results-message-courses">Keine Treffer für Ihre Anfrage</div>
            </div>
        """.trimIndent(), infrastructuresHtml = "")
        val activities = HtmlParser().parseCourses(json.coursesHtml)

        assertThatSingleElement(activities, CourseMyc(
                id = "SEdFOCOPkF",
                category = "Fitnesskurs",
                title = "Doshinkan Karatedo",
                time = "16:00",
                timestamp = "1516446000",
                partner = "City & Country Club Wienerberg, 1100 Wien"
        ))
    }

    @Test(dependsOnMethods = ["parseCourses - simple"])
    fun `parseCourses - courses - integration`() {
        val json = jackson.readValue<ActivitiesMycJson>(readResponse("activities-list-response.html.json"))
        val activities = HtmlParser().parseCourses(json.coursesHtml)
        assertThat(activities).hasSize(5)
    }

    fun `parseInfrastructure - simple`() {
        val json = ActivitiesMycJson(coursesHtml = "", infrastructuresHtml = """
            <li class="js-activities-item js-activities-item--infra " data-type="infrastructure" data-activity="jLDnA1B0Ea" data-location="0" data-date="1516446000">
               undefined
               <span class="text">
                  undefined
                  <table cellpadding="0" cellspacing="0" border="0">
                     undefined
                     <tr>
                        undefined
                        <td class="time">Book Now</td>
                        undefined
                        <td>
                           undefined<span class="cat">Beachvolleyball</span>undefined
                           <h3>
                              Beachvolleyball
                           </h3>
                           undefined
                           <p>undefined<span class="text__partner">Sportzentrum Marswiese, 1170 Wien</span></p>
                        </td>
                     </tr>
                  </table>
               </span>
            </li>
            """.trimIndent())
        val activities = HtmlParser().parseInfrastructure(json.infrastructuresHtml)

        assertThatSingleElement(activities, InfrastructureMyc(
                id = "jLDnA1B0Ea",
                category = "Beachvolleyball",
                title = "Beachvolleyball",
                time = "Book Now",
                partner = "Sportzentrum Marswiese, 1170 Wien"
        ))
    }

    @Test(dependsOnMethods = ["parseInfrastructure - simple"])
    fun `parseInfrastructure - integration`() {
        val json = jackson.readValue<ActivitiesMycJson>(readResponse("activities-list-response.html.json"))
        val activities = HtmlParser().parseInfrastructure(json.infrastructuresHtml)
        assertThat(activities).hasSize(60)
    }

    fun `parseActivity - integration`() {
        val activity = HtmlParser().parseActivity(readResponse("activityDetail.json"))

        assertThat(activity).isEqualTo(ActivityMyc(
                partnerShortName = "triller-crossfit",
                description = "Crosstraining ist viele \u00dcbungen."
        ))
    }

    private fun readResponse(fileName: String) =
            javaClass.getResource("/urclubs/responses/$fileName").readText()

}
