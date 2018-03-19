package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.transactional
import com.github.christophpickl.urclubs.service.sync.testInstance
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import org.testng.annotations.Test

@Test(groups = ["database"])
class ActivityServiceImplTest : DatabaseTest() {

    fun `Given a single partner with a single finished activity When read all finished activities Then return that single element`() {
        val finishedActivity = FinishedActivityDbo.testInstance()
        em.transactional {
            persist(PartnerDbo.testInstance().copy(
                finishedActivities = mutableListOf(finishedActivity)
            ))
        }

        val actual = ActivityServiceImpl(em).readAllFinished()

        assertThatSingleElement(actual, finishedActivity.toFinishedActivity())
    }

}
