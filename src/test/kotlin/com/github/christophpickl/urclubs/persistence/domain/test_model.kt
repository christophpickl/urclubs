package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.urclubs.service.sync.testInstance
import java.time.LocalDateTime

val UpcomingActivityDbo.Companion.testInstance
    get() = UpcomingActivityDbo(
        id = 0L,
        partner = PartnerDbo.testInstance,
        title = "test title",
        date = LocalDateTime.parse("2000-01-01T00:00:00")
    )
