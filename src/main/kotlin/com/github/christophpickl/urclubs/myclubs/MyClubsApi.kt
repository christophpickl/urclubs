package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel

interface MyClubsApi {

    fun loggedUser(): UserMycJson

    fun partners(): List<PartnerHtmlModel>

    // Requests: https://www.myclubs.com/at/de/partner/
    fun partner(shortName: String): PartnerDetailHtmlModel

    fun courses(filter: CourseFilter): List<CourseHtmlModel>

    //    fun infrastructure(): List<InfrastructureMyc>

    // Requests: https://www.myclubs.com/at/de/partner/sporthalle-wien
    //fun singleActivity() ...

    fun activity(filter: ActivityFilter): ActivityHtmlModel

    fun finishedActivities(): List<FinishedActivityHtmlModel>

}

class MyclubsUtil {
    private val baseUrl = "https://www.myclubs.com" // TODO inject, in order to make it fakeable/testable (integration tests with wiremock)

    fun createMyclubsPartnerUrl(shortName: String) = "$baseUrl/at/de/partner/$shortName"
}
