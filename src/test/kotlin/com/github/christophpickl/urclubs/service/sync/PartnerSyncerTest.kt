package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class PartnerSyncerTest {

    private val partnerMyc = PartnerHtmlModel.testInstance()
    private val partner = partnerMyc.toPartner()
    private val insertedPartner = partner.copy(idDbo = 42L)

    private lateinit var myclubs: MyClubsApi
    private lateinit var partnerService: PartnerService

    @BeforeMethod
    fun initMocks() {
        myclubs = mock()
        partnerService = mock()
    }

    fun `Partners Insert - Given empty database and one myclubs partner When sync Then insert and return it`() {
        whenever(partnerService.readAll()).thenReturn(emptyList())
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))
        whenever(partnerService.create(partner)).thenReturn(insertedPartner)

        val result = sync()

        verify(partnerService).create(partner)
        assertThat(result.insertedPartners).containsExactly(insertedPartner)
    }

    fun `Partners Insert - Given partner already in database When sync Then do nothing`() {
        whenever(partnerService.readAll()).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))

        val result = sync()

        verify(partnerService).readAll()
        verifyNoMoreInteractions(partnerService)
        assertThat(result.insertedPartners).isEmpty()
    }

    fun `Partners Delete - Given partner in database but empty myclubs partner When sync Then delete and return it`() {
        whenever(partnerService.readAll()).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(emptyList())

        val result = sync()

        verify(partnerService).delete(insertedPartner)
        assertThat(result.deletedPartners).containsExactly(insertedPartner)

    }

    private fun sync() = PartnerSyncer(myclubs, partnerService).sync()

}
