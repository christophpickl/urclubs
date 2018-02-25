package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo.EMS
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
    id = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    rating = RatingDbo.UNKNOWN,
    deletedByMyc = false,
    address = "testStreet",
    category = EMS,
    favourited = true,
    ignored = false,
    wishlisted = true,
    linkMyclubsSite = "http://test.myclubs.at",
    linkPartnerSite = "http://test.partner.at"
)
