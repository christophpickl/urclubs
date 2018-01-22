package com.github.christophpickl.urclubs.backend

import org.jsoup.Jsoup

class HtmlParser {

    fun parsePartners(html: String) =
            Jsoup.parse(html).select("option").mapNotNull {
                val id = it.attr("value")
                if (id == "") return@mapNotNull null
                PartnerMyc(
                        id = id,
                        title = it.text()
                )
            }

    fun parseActivities(html: String) =
            Jsoup.parse(html).select("li").map { li ->
                ActivityMyc(
                        id = li.attr("data-activity"),
                        time = li.select(".time").text(),
                        title = li.select("h3").text(),
                        category = li.select(".cat").text(),
                        partner = li.select(".text__partner").text(),
                        type = ActivityTypeMyc.byJson(li.attr("data-type"))
                )
            }

}
