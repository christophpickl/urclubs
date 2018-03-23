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
    val linkPartnerSite: String,
    val addresses: List<String>,
    val tags: List<String>, // "Yoga", "Fitnesskurs"
    val upcomingActivities: List<PartnerDetailActivityHtmlModel>
) {
    companion object
}

data class PartnerDetailActivityHtmlModel(
        val idMyc: String,
        val detailLink: String,
        val date: LocalDateTime,
        val title: String,
        val address: String
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
) {
    companion object
}

data class FinishedActivityHtmlModel(
        val date: LocalDateTime,
        val category: String,
        val title: String,
        val locationHtml: String
) {
    companion object
}

data class ProfileHtmlModel(
        val finishedActivities: List<FinishedActivityHtmlModel>
)
