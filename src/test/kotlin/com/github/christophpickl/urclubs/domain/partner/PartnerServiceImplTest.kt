package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.persistTransactional
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.TestDbModule
import com.google.inject.AbstractModule
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Guice
import org.testng.annotations.Test
import javax.inject.Inject

private class PartnerServiceImplTestModule : AbstractModule() {
    override fun configure() {
        install(TestDbModule())
        install(PartnerModule())
    }
}

@Test(groups = ["integration"])
@Guice(modules = [PartnerServiceImplTestModule::class])
class PartnerServiceImplTest : DatabaseTest() {

    @Inject
    private lateinit var service: PartnerService

    private val anyLocationHtml = "anyName<br>anyAddress"

    fun `searchPartner - Given partner exists in DB When search for him Then return him`() {
        val partner = em.persistTransactional(
            PartnerDbo.testInstance().copy(name = "name", addresses = listOf("address"))
        )

        val found = service.searchPartner("${partner.name}<br>${partner.addresses[0]}")

        assertThat(found).isNotNull()
    }

    fun `searchPartner - Given location with HTML encoded ampersand When search for him Then return him`() {
        val partner = em.persistTransactional(
            PartnerDbo.testInstance().copy(name = "City & Country Club", addresses = listOf("address"))
        )

        val found = service.searchPartner("City &amp; Country Club<br>${partner.addresses[0]}")

        assertThat(found).isNotNull()
    }

    fun `searchPartner - Given no partners When search Then return null`() {
        val found = service.searchPartner(anyLocationHtml)

        assertThat(found).isNull()
    }

    @Test(expectedExceptions = [IllegalArgumentException::class])
    fun `searchPartner - When search for invalid location as no linebreak Then throw`() {
        service.searchPartner("invalidAsNoLinebreakTag")
    }

    @Test(expectedExceptions = [IllegalArgumentException::class])
    fun `searchPartner - When search for invalid location as not single linebreak Then throw`() {
        service.searchPartner("got<br>two<br>breaks")
    }

}
