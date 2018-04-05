package com.github.christophpickl.urclubs.domain.activity

import java.time.LocalDateTime

val FinishedActivity.Companion.testInstance
    get() = FinishedActivity(
        title = "test title",
        date = LocalDateTime.parse("2000-01-02T03:00:00")
    )
