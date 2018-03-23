package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import java.time.LocalDateTime

fun Partner.Companion.testInstance() = Partner(
    idDbo = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    addresses = listOf("testStreet"),
    note = "testNote",
    linkMyclubs = "http://test.myclubs.at",
    linkPartner = "http://test.partner.at",
    rating = Rating.UNKNOWN,
    category = Category.EMS,
    maxCredits = Partner.DEFAULT_MAX_CREDITS,
    deletedByMyc = false,
    favourited = true,
    ignored = false,
    wishlisted = true,
    picture = PartnerImage.DefaultPicture,
    finishedActivities = emptyList()
)

fun FinishedActivityDbo.Companion.testInstance() = FinishedActivityDbo(
    title = "testTitle",
    date = LocalDateTime.parse("2000-12-31T09:00:00")
)
