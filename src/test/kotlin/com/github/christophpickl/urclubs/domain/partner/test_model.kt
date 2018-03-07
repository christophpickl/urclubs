package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo.EMS
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
    id = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    address = "testStreet",
    note = "testNote",
    linkMyclubs = "http://test.myclubs.at",
    linkPartner = "http://test.partner.at",
    rating = RatingDbo.UNKNOWN,
    category = EMS,
    maxCredits = Partner.DEFAULT_MAX_CREDITS.toByte(),
    deletedByMyc = false,
    favourited = true,
    ignored = false,
    wishlisted = true,
    picture = null
)
