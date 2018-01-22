package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.backend.MyClubsApi
import com.github.christophpickl.urclubs.backend.PartnerMyc
import com.github.christophpickl.urclubs.backend.testInstance
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.verifyNoMoreInteractions
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class SyncerTest {

    private val partnerMyc = PartnerMyc.testInstance()
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
        whenever(partnerService.fetchAll()).thenReturn(emptyList())
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))
        whenever(partnerService.insert(partner)).thenReturn(insertedPartner)

        val result = sync()

        verify(partnerService).insert(partner)
        assertThat(result.insertedPartners).containsExactly(insertedPartner)
    }

    fun `Partners Insert - Given partner already in database When sync Then do nothing`() {
        whenever(partnerService.fetchAll()).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))

        val result = sync()

        verify(partnerService).fetchAll()
        verifyNoMoreInteractions(partnerService)
        assertThat(result.insertedPartners).isEmpty()
    }

    fun `Partners Delete - Given partner in database but empty myclubs partner When sync Then delete and return it`() {
        whenever(partnerService.fetchAll()).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(emptyList())

        val result = sync()

        verify(partnerService).delete(insertedPartner)
        assertThat(result.deletedPartners).containsExactly(insertedPartner)

    }

    private fun sync() = Syncer(myclubs, partnerService).sync()

}
