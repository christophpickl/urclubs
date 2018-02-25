package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.PartnerDao
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.google.common.eventbus.EventBus
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class PartnerServiceImplTest {

    private lateinit var partnerDao: PartnerDao
    private val anyLocationHtml = "anyLocationHtml"

    @BeforeMethod
    fun initMocks() {
        partnerDao = mock()
    }

    fun `searchPartner - Given partner exists in DB When search for him Then return him`() {
        val partner = PartnerDbo.testInstance().copy(name = "name", address = "address")
        whenever(partnerDao.readAll()).thenReturn(listOf(partner))

        val found = service().searchPartner("${partner.name}\n<br>${partner.address}")

        assertThat(found).isEqualTo(partner.toPartner())
    }

    fun `searchPartner - Given no partners When search Then return null`() {
        whenever(partnerDao.readAll()).thenReturn(emptyList())

        val found = service().searchPartner(anyLocationHtml)

        assertThat(found).isNull()
    }

    private fun service() = PartnerServiceImpl(partnerDao, EventBus())

}
