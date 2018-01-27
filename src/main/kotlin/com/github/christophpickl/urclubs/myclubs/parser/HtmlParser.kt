package com.github.christophpickl.urclubs.myclubs.parser

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.annotations.VisibleForTesting
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HtmlParser {

    private val jackson = jacksonObjectMapper()
    private val finishedActivityDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm")

    fun parsePartners(html: String) =
            Jsoup.parse(html).select("option").mapNotNull { option ->
                val id = option.attr("value")
                if (id == "") return@mapNotNull null
                PartnerHtmlModel(
                        id = id,
                        name = option.text(),
                        shortName = option.attr("data-slug")
                )
            }

    fun parseCourses(html: String) =
            Jsoup.parse(html).select("li").map { li ->
                CourseHtmlModel(
                        id = li.attr("data-activity"),
                        time = li.select(".time").text(), // TODO parse proper time
                        timestamp = li.attr("data-date"),
                        title = li.select("h3").text(),
                        category = li.select(".cat").text(),
                        partner = li.select(".text__partner").text()
                )
            }

    fun parseInfrastructure(html: String) =
            Jsoup.parse(html).select("li").map { li ->
                InfrastructureHtmlModel(
                        id = li.attr("data-activity"),
                        time = li.select(".time").text(),  // TODO "Book Now", "Drop In" => used to infer type (OPEN, RESERVATION_NEEDED => show phone number)
                        title = li.select("h3").text(),
                        category = li.select(".cat").text(),
                        partner = li.select(".text__partner").text()
                )
            }

    fun parseActivity(html: String): ActivityHtmlModel {
        val json = jackson.readValue<ActivityMycJson>(html)

        val description = Jsoup.parse(json.descriptionHtml)

        val partnerHref = description.select("a.text__detail").attr("href")
        val shortName = partnerHref.substring("https://www.myclubs.com/at/de/partner/".length)
        return ActivityHtmlModel(
                partnerShortName = shortName,
                description = description.select("p.text__description").text()
        )
    }

    fun parseProfile(html: String): ProfileHtmlModel {
        val doc = Jsoup.parse(html)
        return ProfileHtmlModel(
                finishedActivities = doc.select("div#area-finished > div.sectiongroup").map {
                    parseFinishedActivityDay(it)
                }.flatten()
        )
    }

    @VisibleForTesting
    fun parseFinishedActivityDay(day: Element): List<FinishedActivityHtmlModel> {
        val dateStringWithDay = day.select("div.sectiongroup__name").text() // Donnerstag, 25.01.2018
        val dateString = dateStringWithDay.substring(dateStringWithDay.indexOf(",") + 1).trim()
        return day.select("div.profile__cards__item").map {
            FinishedActivityHtmlModel(
                    date = parseFinishedActivityDate(dateString, it),
                    category = it.select("div.profile__cards__item__cat").text(),
                    title = it.select("div.profile__cards__item__title").text(),
                    locationHtml = it.select("div.profile__cards__item__location").html()
            )
        }
    }

    fun parsePartner(html: String): PartnerDetailHtmlModel {
        val doc = Jsoup.parse(html)
        return PartnerDetailHtmlModel(
                name = doc.safeSelectFirst("div.storyhl > h1").text(),
                description = doc.safeSelectFirst("p.partner__intro__info__text").text(),
                address = doc.safeSelectFirst("a.partner__places__list__item").text(),
                link = doc.safeSelectFirst("a.partner__intro__info__data__web").text(),
                flags = doc.select("div.tags--small > span.tags__tag").map { it.text() }
        )
    }

    private fun parseFinishedActivityDate(dateString: String, div: Element): LocalDateTime {
        val timeString = div.select("div.profile__cards__item__time__content > span.value").text()
        return LocalDateTime.parse("$dateString-$timeString", finishedActivityDateFormat)
    }

}

private fun Element.safeSelectFirst(selector: String): Element =
        selectFirst(selector) ?: throw Exception("Could not find HTML part by selector: $selector\n$this")

private data class ActivityMycJson(
        @JsonProperty("html")
        val descriptionHtml: String,
        @JsonProperty("booking")
        val bookingHtml: String
)
