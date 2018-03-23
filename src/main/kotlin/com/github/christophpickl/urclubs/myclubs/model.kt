package com.github.christophpickl.urclubs.myclubs

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import com.github.christophpickl.urclubs.domain.activity.ActivityType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class UserMycJson(
        @JsonProperty("user_id")
        val id: String,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("firstname")
        val firstName: String,
        @JsonProperty("lastname")
        val lastName: String
) {
    companion object
}

data class ActivitiesMycJson(
        // boundingBox
        // currentRegion
        // pins
        @JsonProperty("courses")
        val coursesHtml: String,
        @JsonProperty("infrastructures")
        val infrastructuresHtml: String
)


data class FilterMycJson(
        val categories: List<String> = emptyList(),
        val date: List<String>,
        val time: List<String>,
        val favourite: Boolean = false,
        val city: String = "wien",
        val partner: String = "",
        val type: List<ActivityTypeMyc> = ActivityTypeMyc.all
) {
    companion object
}

enum class ActivityTypeMyc(
        @get:JsonValue val json: String
) {

    Course("course"),
    Infrastructure("infrastructure");

    companion object {

        val all = values().toList()

        private val typesByJson by lazy {
            ActivityTypeMyc.values().associateBy { it.json }
        }

        fun byJson(search: String) = typesByJson[search]
                ?: throw IllegalArgumentException("Unknown activity type '$search'!")
    }
}

data class CourseFilter(
        val start: LocalDateTime,
        val end: LocalDateTime
) {
    init {
        if (start.isAfter(end)) {
            throw IllegalArgumentException("Start date ($start) must not be after end date ($end)!")
        }
        if (start.dayOfYear != end.dayOfYear || start.year != end.year) {
            throw IllegalArgumentException("Start date ($start) and end date ($end) must only differ in time!")
        }
    }
}


private val filterDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val filterTimeFormat = DateTimeFormatter.ofPattern("HH:mm")

fun CourseFilter.toFilterMycJson() = FilterMycJson(
        date = listOf(filterDateFormat.format(start)),
        time = listOf(filterTimeFormat.format(start), filterTimeFormat.format(end)),
        type = listOf(ActivityTypeMyc.Course)
)

data class ActivityFilter(
        val activityId: String, // "Pf5FowjC0n",
        val timestamp: String, // "1516705200"
        val type: ActivityType // [ Course, Infrastructure ]
)

fun ActivityType.toActivityTypeMyc() = when (this) {
    ActivityType.Course -> ActivityTypeMyc.Course
    ActivityType.Infrastructure -> ActivityTypeMyc.Infrastructure
}
