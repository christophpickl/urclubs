package com.github.christophpickl.urclubs.myclubs

import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
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

fun UserMycJson.Companion.testInstance() = UserMycJson(id ="testId", email = "testEmail", firstName = "testFirstName", lastName = "testLastName")
