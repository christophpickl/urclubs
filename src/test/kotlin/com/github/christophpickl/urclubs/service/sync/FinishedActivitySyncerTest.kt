package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.captureUpdate
import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.time.LocalDateTime

@Test
class FinishedActivitySyncerTest {

    private val date1 = LocalDateTime.parse("2000-12-31T09:00:00")
    private val finishedActivityHtml = FinishedActivityHtmlModel.testInstance().copy(date = date1)
    private val finishedActivity = finishedActivityHtml.toFinishedActivity()
    private val partnerWithoutFinishedActivities = Partner.testInstance().copy(finishedActivities = emptyList())

    private lateinit var myclubs: MyClubsApi
    private lateinit var activityService: ActivityService
    private lateinit var partnerService: PartnerService
    private lateinit var syncer: FinishedActivitySyncer

    @BeforeMethod
    fun initState() {
        myclubs = mock()
        activityService = mock()
        partnerService = mock()
        syncer = FinishedActivitySyncer(myclubs, activityService, partnerService)
    }

    fun `Given single finished activity from myclubs When sync Then one activity should have been inserted`() {
        whenever(myclubs.finishedActivities()).thenReturn(listOf(finishedActivityHtml))
        whenever(partnerService.searchPartner(finishedActivityHtml.locationHtml)).thenReturn(partnerWithoutFinishedActivities)
        whenever(activityService.readAllFinished()).thenReturn(emptyList())

        val report = syncer.sync()

        val updatedPartner = partnerService.captureUpdate()
        assertThat(updatedPartner.finishedActivities).containsExactly(finishedActivity)
        assertThat(report.inserted).containsExactly(finishedActivity)
    }

    fun `Given single finished activity from myclubs and same from database When sync Then nothing happened`() {
        whenever(myclubs.finishedActivities()).thenReturn(listOf(finishedActivityHtml))
        whenever(partnerService.searchPartner(finishedActivityHtml.locationHtml)).thenReturn(partnerWithoutFinishedActivities)
        whenever(activityService.readAllFinished()).thenReturn(listOf(finishedActivity))

        val report = syncer.sync()

        assertThat(report.inserted).isEmpty()
    }

}
