package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import java.time.LocalDateTime

val Partner.Companion.testInstance
    get() = Partner(
    idDbo = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    note = "testNote",
    linkMyclubs = "http://test.myclubs.at",
    linkPartner = "http://test.partner.at",
    rating = Rating.UNKNOWN,
    category = Category.EMS,
    maxCredits = Partner.DEFAULT_MAX_CREDITS,
    dateInserted = LocalDateTime.now(),
    dateDeleted = null,
    favourited = true,
    ignored = false,
    wishlisted = true,
    addresses = listOf("testStreet"),
    tags = listOf("tag"),
    finishedActivities = emptyList(),
    picture = PartnerImage.DefaultPicture
)

val FinishedActivityDbo.Companion.testInstance
    get() = FinishedActivityDbo(
    title = "testTitle",
    date = LocalDateTime.parse("2000-12-31T09:00:00")
)
