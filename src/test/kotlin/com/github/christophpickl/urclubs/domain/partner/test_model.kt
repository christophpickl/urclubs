package com.github.christophpickl.urclubs.domain.partner

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
        id = 0L,
        idMyc = "testIdMyc",
        name = "testName",
        shortName = "testShortName",
        rating = RatingDbo.UNKNOWN
)
