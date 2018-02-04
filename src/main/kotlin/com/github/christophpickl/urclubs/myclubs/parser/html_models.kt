package com.github.christophpickl.urclubs.myclubs.parser

import java.time.LocalDateTime

data class PartnerHtmlModel(
        val id: String,
        val shortName: String,
        val name: String
) {
    companion object
}

data class PartnerDetailHtmlModel(
        val name: String,
        val description: String,
        val link: String,
        val address: String,
        val flags: List<String>
)

data class CourseHtmlModel(
        val id: String,
        val time: String,
        val timestamp: String,
        val title: String,
        val partner: String,
        val category: String
)

data class InfrastructureHtmlModel(
        val id: String,
        val time: String,
        val title: String,
        val partner: String,
        val category: String
)

data class ActivityHtmlModel(
        val partnerShortName: String,
        val description: String
)

data class FinishedActivityHtmlModel(
        val date: LocalDateTime,
        val category: String,
        val title: String,
        val locationHtml: String
)

data class ProfileHtmlModel(
        val finishedActivities: List<FinishedActivityHtmlModel>
)