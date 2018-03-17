package com.github.christophpickl.urclubs.domain.activity

import java.time.LocalDateTime

data class FinishedActivity(
        val idMyc: String,
        val date: LocalDateTime
// title?
)

enum class ActivityType {
    Course,
    Infrastructure
}
