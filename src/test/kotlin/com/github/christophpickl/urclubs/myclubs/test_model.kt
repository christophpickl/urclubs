package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailActivityHtmlModel
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
    name = "iYoga",
    description = "some yoga partner",
    linkPartnerSite = "www.yoga.at",
    addresses = listOf("Hauptyogastrasse 1"),
    tags = listOf("Yoga"),
    upcomingActivities = listOf<PartnerDetailActivityHtmlModel>()
)
