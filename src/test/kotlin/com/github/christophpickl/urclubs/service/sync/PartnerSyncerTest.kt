package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.MyclubsUtil
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
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

    private fun Partner.withIdSet() = copy(idDbo = 42)

    private lateinit var myclubs: MyClubsApi
    private lateinit var partnerService: PartnerService

    @BeforeMethod
    fun initMocks() {
        myclubs = mock()
        partnerService = mock()
    }

    fun `Partners Insert - Given empty database and one myclubs partner When sync Then insert and return it`() {
        val partnerDetail = PartnerDetailHtmlModel.testInstance()
        val partnerMyc = PartnerHtmlModel.testInstance()
        val partner = partnerMyc.toPartner().enhance(partnerDetail)
        val insertedPartner = partner.withIdSet()

        whenever(partnerService.readAll()).thenReturn(emptyList())
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))
        whenever(myclubs.partner(partnerMyc.shortName)).thenReturn(partnerDetail)
        whenever(partnerService.create(partner)).thenReturn(insertedPartner)

        val result = sync()

        verify(partnerService).create(partner)
        assertThat(result.insertedPartners).containsExactly(insertedPartner)
    }

    fun `Partners Insert Autodetect Category - Given empty database and one myclubs partner When sync Then insert and return it`() {
        val partnerDetail = PartnerDetailHtmlModel.testInstance()
        val partnerMyc = PartnerHtmlModel.testInstance()
        val partner = partnerMyc.toPartner().enhance(partnerDetail)
        val insertedPartner = partner.withIdSet()

        whenever(partnerService.readAll()).thenReturn(emptyList())
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))
        whenever(myclubs.partner(partnerMyc.shortName)).thenReturn(partnerDetail)
        whenever(partnerService.create(partner)).thenReturn(insertedPartner)

        val result = sync()

        verify(partnerService).create(partner)
        assertThat(result.insertedPartners).containsExactly(insertedPartner)
    }

    fun `Partners Insert - Given partner already in database When sync Then do nothing`() {
        val partnerDetail = PartnerDetailHtmlModel.testInstance()
        val partnerMyc = PartnerHtmlModel.testInstance()
        val insertedPartner = partnerMyc.toPartner().withIdSet()

        whenever(partnerService.readAll(includeIgnored = true)).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(listOf(partnerMyc))
        whenever(myclubs.partner(partnerMyc.shortName)).thenReturn(partnerDetail)

        val result = sync()

        verify(partnerService).readAll(includeIgnored = true)
        verifyNoMoreInteractions(partnerService)
        assertThat(result.insertedPartners).isEmpty()
    }

    fun `Partners Delete - Given partner in database but empty myclubs partner When sync Then delete and return it`() {
        val insertedPartner = PartnerHtmlModel.testInstance().toPartner().withIdSet()

        whenever(partnerService.readAll(includeIgnored = true)).thenReturn(listOf(insertedPartner))
        whenever(myclubs.partners()).thenReturn(emptyList())

        val result = sync()

        val deletedPartner = insertedPartner.copy(deletedByMyc = true)
        verify(partnerService).update(deletedPartner)
        assertThat(result.deletedPartners).containsExactly(deletedPartner)
    }

    private fun sync() = PartnerSyncer(myclubs, partnerService, MyclubsUtil()).sync()

    private fun Partner.enhance(detailed: PartnerDetailHtmlModel) = copy(
        addresses = detailed.addresses,
        linkPartner = detailed.linkPartnerSite,
        linkMyclubs = "https://www.myclubs.com/at/de/partner/$shortName",
        tags = detailed.tags
    )

}
