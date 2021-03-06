package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel

interface MyClubsApi {

    fun loggedUser(): UserMycJson

    fun partners(): List<PartnerHtmlModel>

    // https://www.myclubs.com/at/de/partner/$shortName
    fun partner(shortName: String): PartnerDetailHtmlModel

    fun finishedActivities(): List<FinishedActivityHtmlModel>

    fun activity(filter: ActivityFilter): ActivityHtmlModel

    fun courses(filter: CourseFilter): List<CourseHtmlModel>

}

class MyclubsUtil {
    private val baseUrl = "https://www.myclubs.com"

    fun createMyclubsPartnerUrl(shortName: String) = "$baseUrl/at/de/partner/$shortName"
}
