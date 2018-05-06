package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.UpcomingActivityDaoImpl
import com.github.christophpickl.urclubs.persistence.transactional
import com.github.christophpickl.urclubs.service.sync.testInstance
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import org.testng.annotations.Test

@Test(groups = ["database"])
class ActivityServiceImplTest : DatabaseTest() {

    private val service by lazy { ActivityServiceImpl(em, UpcomingActivityDaoImpl(em)) }
    private val finishedActivityDbo = FinishedActivityDbo.testInstance

    fun `Given a single partner with a single finished activity When read all finished activities Then return that single element`() {
        savePartnerWithFinishedActivies(finishedActivityDbo)

        val actual = service.readAllFinished()

        assertThatSingleElement(actual, finishedActivityDbo.toFinishedActivity())
    }

    private fun savePartnerWithFinishedActivies(vararg activities: FinishedActivityDbo) {
        em.transactional {
            persist(PartnerDbo.testInstance.copy(
                finishedActivities = activities.toMutableList()
            ))
        }
    }

}
