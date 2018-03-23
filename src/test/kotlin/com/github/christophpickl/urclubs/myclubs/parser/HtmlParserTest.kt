package com.github.christophpickl.urclubs.myclubs.parser

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.christophpickl.urclubs.myclubs.ActivitiesMycJson
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import org.assertj.core.api.Assertions.assertThat
import org.jsoup.Jsoup
import org.testng.annotations.Test
import java.time.LocalDate
import java.time.LocalDateTime

@Test
class HtmlParserTest {

    private val jackson = jacksonObjectMapper().apply {
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    fun `parsePartners - read valid entry`() {
        assertThatSingleElement(HtmlParser().parsePartners("""<option value="myId" data-slug="short-name">my name</option>"""),
            PartnerHtmlModel(
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
        """, infrastructuresHtml = "")
        val activities = HtmlParser().parseCourses(json.coursesHtml)

        assertThatSingleElement(activities, CourseHtmlModel(
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
            """)
        val activities = HtmlParser().parseInfrastructure(json.infrastructuresHtml)

        assertThatSingleElement(activities, InfrastructureHtmlModel(
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

        assertThat(activity).isEqualTo(ActivityHtmlModel(
            partnerShortName = "triller-crossfit",
            description = "Crosstraining ist viele \u00dcbungen."
        ))
    }

    fun `parseFinishedActivity - single activity`() {
        val div = Jsoup.parse("""
            <div class="sectiongroup">
                <div class="sectiongroup__name">Donnerstag, 25.01.2018</div>
                <div class="sectiongroup__content">
                    <div class="profile__cards__item">
                        <div class="profile__cards__item__close">
                            <i class="icon icon--close-card js-close-cards-item"></i>
                        </div>
                        <div class="profile__cards__item__preview">
                            <div class="profile__cards__item__time">
                                <div class="profile__cards__item__time__content">
                                    <i class="icon icon--clock"></i>
                                    <span class="value">08:00</span>
                                </div>
                            </div>
                            <div class="profile__cards__item__content">
                                <div class="profile__cards__item__cat">Fitnesskurs</div>
                                <div class="profile__cards__item__title">MovNat</div>
                                <div class="profile__cards__item__location">SPORTHALLE WIEN<br>Fuhrmannsgasse 18, 1080 Wien</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """).body()

        val activity = HtmlParser().parseFinishedActivityDay(div)

        assertThatSingleElement(activity, FinishedActivityHtmlModel(
            date = LocalDateTime.parse("2018-01-25T08:00:00"),
            category = "Fitnesskurs",
            title = "MovNat",
            locationHtml = "SPORTHALLE WIEN<br>Fuhrmannsgasse 18, 1080 Wien"
        ))
    }

    fun `parseFinishedActivity - multiple activities`() {
        val div = Jsoup.parse("""
            <div class="sectiongroup">
                <div class="sectiongroup__name">Mittwoch, 10.01.2018</div>
                <div class="sectiongroup__content">
                    <div class="profile__cards__item">
                        <div class="profile__cards__item__close">
                            <i class="icon icon--close-card js-close-cards-item"></i>
                        </div>
                        <div class="profile__cards__item__preview">
                            <div class="profile__cards__item__time">
                                <div class="profile__cards__item__time__content">
                                    <i class="icon icon--clock"></i>
                                    <span class="value">16:00</span>
                                </div>
                            </div>
                            <div class="profile__cards__item__content">
                                <div class="profile__cards__item__cat">EMS</div>
                                <div class="profile__cards__item__title">EMS-Training</div>
                                <div class="profile__cards__item__location">Bodystreet Convalere<br>Taborstraße 33, 1020 Wien</div>
                            </div>
                        </div>
                    </div>
                    <div class="profile__cards__item">
                        <div class="profile__cards__item__close">
                            <i class="icon icon--close-card js-close-cards-item"></i>
                        </div>
                        <div class="profile__cards__item__preview">
                            <div class="profile__cards__item__time">
                                <div class="profile__cards__item__time__content">
                                    <i class="icon icon--clock"></i>
                                    <span class="value">08:00</span>
                                </div>
                            </div>
                            <div class="profile__cards__item__content">
                                <div class="profile__cards__item__cat">Crosstraining</div>
                                <div class="profile__cards__item__title">Strength Advanced</div>
                                <div class="profile__cards__item__location">SPORTHALLE WIEN<br>Fuhrmannsgasse 18, 1080 Wien</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            """).body()

        val activities = HtmlParser().parseFinishedActivityDay(div)

        assertThat(activities).hasSize(2)
        assertThat(activities[0]).isEqualTo(FinishedActivityHtmlModel(
            date = LocalDateTime.parse("2018-01-10T16:00:00"),
            category = "EMS",
            title = "EMS-Training",
            locationHtml = "Bodystreet Convalere    <br>Taborstraße 33, 1020 Wien"
        ))
        assertThat(activities[1]).isEqualTo(FinishedActivityHtmlModel(
            date = LocalDateTime.parse("2018-01-10T08:00:00"),
            category = "Crosstraining",
            title = "Strength Advanced",
            locationHtml = "SPORTHALLE WIEN<br>Fuhrmannsgasse 18, 1080 Wien"
        ))
    }

    fun `parseProfile - integration`() {
        val profile = HtmlParser().parseProfile(readResponse("profile.html"))

        assertThat(profile.finishedActivities).hasSize(67)
    }

    fun `parsePartner - integration`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.html"))

        assertThat(partner).isEqualTo(PartnerDetailHtmlModel(
            name = "Hotpod Yoga Vienna",
            description = "Some description.",
            linkPartnerSite = "http://www.hotpodyoga.com/at/yoga-classes/vienna/",
            addresses = listOf("Margaretenstraße 70/2/2, 1050 Wien"),
            tags = listOf("Bikram & Hot Yoga", "Yoga"),
            upcomingActivities = listOf(PartnerDetailActivityHtmlModel(
                idMyc = "meqR6C5d0m",
                detailLink = "https://www.myclubs.com/at/de/aktivitaeten/at/wien/meqR6C5d0mc",
                date = LocalDateTime.parse("2018-01-27T10:30:00"),
                title = "Hotpod Flow English - Beginner/Intermediate",
                address = "Hotpod Yoga Vienna, Margaretenstraße 70/2/2, 1050 Wien"
            ))
        ))
    }

    fun `parsePartner - multi activities`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.multi_activities.html"))

        assertThat(partner.upcomingActivities).hasSize(9)
    }

    fun `parsePartner - multi addresses`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.multi_address.html"))

        assertThat(partner.addresses).hasSize(3)
    }

    fun `parsePartner - integration without address`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.without_address.html"))

        assertThat(partner.addresses).isEmpty()
    }

    fun `parsePartner - integration upcoming is only book now`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.upcoming_booknow.html"))

        assertThat(partner.upcomingActivities).isEmpty()
    }

    fun `parsePartner - integration book now and drop in time Then set time to zero`() {
        val partner = HtmlParser().parsePartner(readResponse("partner.book_now.html"))

        assertThat(partner.upcomingActivities).anyMatch { it.idMyc == "idMycDropin" && it.date == LocalDateTime.parse("2018-03-08T00:00") }
        assertThat(partner.upcomingActivities).anyMatch { it.idMyc == "idMycBooknow" && it.date == LocalDateTime.parse("2018-03-08T00:00") }

    }

    fun `parseDateFromUpcomingActivityTitle`() {
        mapOf(
            "Heute, 27.01.2018" to LocalDate.parse("2018-01-27"),
            "Morgen, 24.02.2018" to LocalDate.parse("2018-02-24"),
            "Montag, 26.02.2018" to LocalDate.parse("2018-02-26")
        ).forEach { inputString, expectedDate ->
            assertThat(HtmlParser().parseDateFromUpcomingActivityTitle(inputString)).`as`("Failed to parse '$inputString'.").isEqualTo(expectedDate)
        }
    }

    fun `parseAndCombineDateTime`() {
        assertThat(HtmlParser().parseAndCombineDateTime(LocalDate.parse("2018-01-27"), "10:30")).isEqualTo(LocalDateTime.parse("2018-01-27T10:30:00"))
    }

    private fun readResponse(fileName: String) =
        javaClass.getResource("/urclubs/responses/$fileName").readText()

}
