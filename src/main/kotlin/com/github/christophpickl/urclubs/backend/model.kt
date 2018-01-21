package com.github.christophpickl.urclubs.backend

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class UserJson(
        @JsonProperty("user_id")
        val id: String,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("firstname")
        val firstName: String,
        @JsonProperty("lastname")
        val lastName: String
)

data class ActivitiesJson(
        // boundingBox
        // currentRegion
        // pins
        @JsonProperty("courses")
        val coursesHtml: String,
        @JsonProperty("infrastructures")
        val infrastructuresHtml: String
)

data class FilterJson(
        val categories: List<String> = emptyList(),
        val date: List<String>,
        val time: List<String>,
        val favourite: Boolean = false,
        val city: String = "wien",
        val partner: String = "",
        val type: List<ActivityType> = ActivityType.all
) {
    companion object
}

data class Partner(
        val id: String,
        val title: String
)

data class Activity (
        val id: String,
        val time: String,
        val title: String,
        val partner: String,
        val category: String,
        val type: ActivityType
)

enum class ActivityType(
        @get:JsonValue val json: String
) {

    Course("course"),
    Infrastructure("infrastructure");

    companion object {

        val all = values().toList()

        private val typesByJson by lazy {
            ActivityType.values().associateBy { it.json }
        }

        fun byJson(search: String) = typesByJson[search] ?:
        throw IllegalArgumentException("Unknown activity type '$search'!")
    }
}
