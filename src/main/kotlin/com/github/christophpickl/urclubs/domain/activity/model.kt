package com.github.christophpickl.urclubs.domain.activity

data class Activity(
        val idMyc: String
)

enum class ActivityType {
    Course,
    Infrastructure
}
