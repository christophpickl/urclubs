package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class FinishedActivitySyncerTest {

    private val finishedActivityHtml = FinishedActivityHtmlModel.testInstance()
    private val partner = Partner.testInstance()

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
        whenever(partnerService.searchPartner(finishedActivityHtml.locationHtml)).thenReturn(partner)

        val report = syncer.sync()

        Assertions.assertThat(report.inserted).hasSize(1)
    }
}
