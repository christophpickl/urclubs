package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.domain.activity.FinishedActivity
import com.github.christophpickl.urclubs.domain.activity.testInstance
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import java.time.LocalDateTime

@Test
class PartnerTest {

    private val now get() = LocalDateTime.now()

    fun `lastVisitInDays - Given no activities Then return null`() {
        val partner = partnerWithFinishedActivities(emptyList())

        assertThat(partner.lastVisitInDays).isNull()
    }

    fun `lastVisitInDays - Given one artificial activity Then return null`() {
        val partner = partnerWithFinishedActivities(listOf(FinishedActivity.artificialInstance))

        assertThat(partner.lastVisitInDays).isNull()
    }

    fun `lastVisitInDays - Given one activity Then return dates until then`() {
        val partner = partnerWithFinishedActivities(listOf(FinishedActivity.testInstance.copy(date = now.minusDays(1))))

        assertThat(partner.lastVisitInDays).isEqualTo(1)
    }

    fun `lastVisitInDays - Given two activities first newer Then return days from newer`() {
        val partner = partnerWithFinishedActivities(listOf(
            FinishedActivity.testInstance.copy(date = now.minusDays(1)),
            FinishedActivity.testInstance.copy(date = now.minusDays(2))
        ))

        assertThat(partner.lastVisitInDays).isEqualTo(1)
    }

    fun `lastVisitInDays - Given two activities second newer Then return days from newer`() {
        val partner = partnerWithFinishedActivities(listOf(
            FinishedActivity.testInstance.copy(date = now.minusDays(2)),
            FinishedActivity.testInstance.copy(date = now.minusDays(1))
        ))

        assertThat(partner.lastVisitInDays).isEqualTo(1)
    }

    private fun partnerWithFinishedActivities(activities: List<FinishedActivity>) =
        Partner.testInstance.copy(finishedActivities = activities)
}
