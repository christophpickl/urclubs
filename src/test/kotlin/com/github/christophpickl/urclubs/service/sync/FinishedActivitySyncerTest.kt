package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.activity.ActivityServiceImpl
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerServiceImpl
import com.github.christophpickl.urclubs.domain.partner.captureUpdate
import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.github.christophpickl.urclubs.persistence.domain.PartnerDaoImpl
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.UpcomingActivityDaoImpl
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.google.common.eventbus.EventBus
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class FinishedActivitySyncerTest {

    private val finishedActivityHtml = FinishedActivityHtmlModel.testInstance().copy(title = "fact")
    private val finishedActivity = finishedActivityHtml.toFinishedActivity()

    private val partner = Partner.testInstance.copy(idDbo = 42, finishedActivities = emptyList())

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
        whenever(partnerService.read(partner.idDbo)).thenReturn(partner)
        whenever(activityService.readAllFinished()).thenReturn(emptyList())

        val report = syncer.sync()

        val updatedPartner = partnerService.captureUpdate()
        assertThat(updatedPartner.finishedActivities).containsExactly(finishedActivity)
        assertThat(report.inserted).containsExactly(finishedActivity)
    }

    fun `Given single finished activity from myclubs and same from database When sync Then nothing happened`() {
        whenever(myclubs.finishedActivities()).thenReturn(listOf(finishedActivityHtml))
        whenever(partnerService.searchPartner(finishedActivityHtml.locationHtml)).thenReturn(partner)
        whenever(activityService.readAllFinished()).thenReturn(listOf(finishedActivity))

        val report = syncer.sync()

        assertThat(report.inserted).isEmpty()
    }

}

@Test
class FinishedActivitySyncerDbTest : DatabaseTest() {

    fun `Given two finished activities for one partner When sync Then two activities should be set`() {
        val partnerName = "pdiddy"
        val partnerAddress = "wien"
        val partnerLocationHtml = "$partnerName<br>$partnerAddress"
        val activityHtml1 = FinishedActivityHtmlModel.testInstance().copy(title = "fact1", locationHtml = partnerLocationHtml)
        val activityHtml2 = FinishedActivityHtmlModel.testInstance().copy(title = "fact2", locationHtml = partnerLocationHtml)

        val bus = EventBus("testBus")
        val myclubs = mock<MyClubsApi>()
        whenever(myclubs.finishedActivities()).thenReturn(listOf(activityHtml1, activityHtml2))

        val dao = PartnerDaoImpl(em)
        val activityService = ActivityServiceImpl(em, UpcomingActivityDaoImpl(em))
        val partnerService = PartnerServiceImpl(dao, bus)
        val syncer = FinishedActivitySyncer(myclubs, activityService, partnerService)

        val partner = dao.create(PartnerDbo.testInstance.copy(
            name = partnerName,
            addresses = mutableListOf(partnerAddress),
            finishedActivities = mutableListOf()
        ))

        val report = syncer.sync()

        assertThat(report.inserted).hasSize(2)
        val updatedPartner = dao.read(partner.id)!!
        assertThat(updatedPartner.finishedActivities).hasSize(2)
    }


}
