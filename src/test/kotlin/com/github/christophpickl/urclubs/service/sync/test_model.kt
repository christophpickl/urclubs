package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo
import com.github.christophpickl.urclubs.persistence.toTimestamp
import java.time.LocalDateTime


val PartnerDbo.Companion.testInstance
    get() = PartnerDbo(
    id = 0L,
    idMyc = "testIdMyc",
    name = "testName",
    shortName = "testShortName",
    note = "testNote",
    linkMyclubs = "http://test.myclubs.at",
    linkPartner = "http://test.partner.at",
    rating = RatingDbo.UNKNOWN,
    category = CategoryDbo.EMS,
    maxCredits = Partner.DEFAULT_MAX_CREDITS.toByte(),
    dateInserted = LocalDateTime.now().toTimestamp(),
    dateDeleted = null,
    favourited = true,
    ignored = false,
    wishlisted = true,
    picture = null,
    addresses = mutableListOf("testStreet"),
    tags = mutableListOf("testTag"),
    finishedActivities = mutableListOf()
)
