package com.github.christophpickl.urclubs.backend

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue

data class UserMycJson(
        @JsonProperty("user_id")
        val id: String,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("firstname")
        val firstName: String,
        @JsonProperty("lastname")
        val lastName: String
)

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

data class PartnerMyc(
        val id: String,
        val title: String
) {
    companion object
}

data class ActivityMyc(
        val id: String,
        val time: String,
        val title: String,
        val partner: String,
        val category: String,
        val type: ActivityTypeMyc
)

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

        fun byJson(search: String) = typesByJson[search] ?:
        throw IllegalArgumentException("Unknown activity type '$search'!")
    }
}
