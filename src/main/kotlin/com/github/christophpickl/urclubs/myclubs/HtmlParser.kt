package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup

class HtmlParser {

    private val jackson = jacksonObjectMapper()

    fun parsePartners(html: String) =
            Jsoup.parse(html).select("option").mapNotNull { option ->
                val id = option.attr("value")
                if (id == "") return@mapNotNull null
                PartnerMyc(
                        id = id,
                        name = option.text(),
                        shortName = option.attr("data-slug")
                )
            }

    fun parseCourses(html: String) =
            Jsoup.parse(html).select("li").map { li ->
                CourseMyc(
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
                InfrastructureMyc(
                        id = li.attr("data-activity"),
                        time = li.select(".time").text(), // TODO parse proper time
                        title = li.select("h3").text(),
                        category = li.select(".cat").text(),
                        partner = li.select(".text__partner").text()
                )
            }

    fun parseActivity(html: String): ActivityMyc {
        val json = jackson.readValue<ActivityMycJson>(html)

        val description = Jsoup.parse(json.descriptionHtml)

        val partnerHref = description.select("a.text__detail").attr("href")
        val shortName = partnerHref.substring("https://www.myclubs.com/at/de/partner/".length)
        return ActivityMyc(
                partnerShortName = shortName,
                description = description.select("p.text__description").text()
        )
    }
}
