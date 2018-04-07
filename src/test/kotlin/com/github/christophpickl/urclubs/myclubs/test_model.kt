package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import java.time.LocalDateTime

fun PartnerHtmlModel.Companion.testInstance() = PartnerHtmlModel(
    id = "testMycId",
    name = "testTitle",
    shortName = "testShortName"
)

fun FinishedActivityHtmlModel.Companion.testInstance() = FinishedActivityHtmlModel(
    date = LocalDateTime.parse("2000-12-31T09:00:00"),
    category = "Yoga",
    title = "Super Yoga",
    locationHtml = "Yoga<br>Wien"
)

fun UserMycJson.Companion.testInstance() = UserMycJson(id = "testId", email = "testEmail", firstName = "testFirstName", lastName = "testLastName")

fun ActivityHtmlModel.Companion.testInstance() = ActivityHtmlModel(partnerShortName = "testPartnerShortName", description = "testDescription")

fun PartnerDetailHtmlModel.Companion.testInstance() = PartnerDetailHtmlModel(
    name = "iTest",
    description = "some test partner",
    linkPartnerSite = "www.test.at",
    addresses = listOf("Hauptteststrasse 1"),
    tags = listOf("test"),
    upcomingActivities = listOf()
)

val CourseHtmlModel.Companion.testInstance
    get() = CourseHtmlModel(
        id = "testId",
        timestamp = "testTimestamp",
        time = "testTime",
        title = "testTitle",
        partner = "testPartner",
        category = "testCategory"
    )
