package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import java.time.LocalDateTime

data class FinishedActivity(
    val title: String,
    val date: LocalDateTime
) {
    companion object {
        val artificialInstance = FinishedActivity(
            title = "artificial activity",
            date = LocalDateTime.parse("2000-01-01T00:00:00")
        )
    }
}

data class UpcomingActivity(
    val title: String,
    val date: LocalDateTime
)

enum class ActivityType {
    Course,
    Infrastructure
}

fun FinishedActivityDbo.toFinishedActivity() = FinishedActivity(
    title = title,
    date = date
)

fun FinishedActivity.toFinishedActivityDbo() = FinishedActivityDbo(
    title = title,
    date = date
)
