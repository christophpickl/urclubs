package com.github.christophpickl.urclubs.domain.activity

import java.time.LocalDateTime

data class FinishedActivity(
    val title: String,
    val date: LocalDateTime
)

enum class ActivityType {
    Course,
    Infrastructure
}
