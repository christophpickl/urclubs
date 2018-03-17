package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo

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
    picture = Picture.DefaultPicture,
    finishedActivities = emptyList()
)

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
    id = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    addresses = listOf("testStreet"),
    note = "testNote",
    linkMyclubs = "http://test.myclubs.at",
    linkPartner = "http://test.partner.at",
    rating = RatingDbo.UNKNOWN,
    category = CategoryDbo.EMS,
    maxCredits = Partner.DEFAULT_MAX_CREDITS.toByte(),
    deletedByMyc = false,
    favourited = true,
    ignored = false,
    wishlisted = true,
    picture = null
)
