package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import java.time.LocalDateTime

object StubbedMyClubsApi : MyClubsApi {

    private val partner1 = PartnerHtmlModel(
        id = "id1",
        shortName = "short1",
        name = "name1"
    )
    private val partner1Detail = PartnerDetailHtmlModel(
        name = partner1.name,
        description = "description1",
        linkPartnerSite = "",
        addresses = listOf("home1"),
        flags = emptyList(),
        upcomingActivities = emptyList()
    )

    override fun loggedUser() = UserMycJson(
        id = "id",
        email = "email@home.at",
        firstName = "First",
        lastName = "Last"
    )

    override fun partners() = listOf(partner1)

    override fun partner(shortName: String) = partner1Detail

    override fun courses(filter: CourseFilter) = emptyList<CourseHtmlModel>()

    override fun activity(filter: ActivityFilter) = ActivityHtmlModel(
        partnerShortName = "",
        description = ""
    )

    override fun finishedActivities() = listOf(
        FinishedActivityHtmlModel(
            date = LocalDateTime.now(),
            category = "cat",
            title = "some title",
            locationHtml = "${partner1Detail.name}<br>${partner1Detail.addresses.first()}"
        )
    )

}
