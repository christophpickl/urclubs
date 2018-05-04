package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.UpcomingActivityDbo

fun UpcomingActivity.toUpcomingActivityDbo(partner: PartnerDbo) = UpcomingActivityDbo(
    id = idDbo,
    title = title,
    date = date,
    partner = partner
)

fun UpcomingActivityDbo.toUpcomingActivity() = UpcomingActivity(
    idDbo = id,
    title = title,
    date = date
)
