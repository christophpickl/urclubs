package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.singleEntryIsEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class PartnerObjectDbDaoTest : DatabaseTest() {
    
    private val partner = PartnerDbo.testInstance()

    fun `Given a partner is stored When fetch all partners Then that partner is returned`() {
        em.transactional { persist(partner) }

        val partners = PartnerObjectDbDao(em).fetchAll()

        assertThat(partners).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `When insert partner Then expect partner to be inserted in database`() {
        PartnerObjectDbDao(em).insert(partner)

        assertThat(em.createQuery("SELECT p FROM PartnerDbo p", PartnerDbo::class.java).resultList)
                .singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

}

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
        name = "testName"
)
