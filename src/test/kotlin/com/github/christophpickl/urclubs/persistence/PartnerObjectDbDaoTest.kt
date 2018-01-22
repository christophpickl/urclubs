package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.singleEntryIsEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class PartnerObjectDbDaoTest : DatabaseTest() {

    private val partner = PartnerDbo.testInstance()
    private val partner1 = PartnerDbo.testInstance().copy(idMyc = "myc1")
    private val partner2 = PartnerDbo.testInstance().copy(idMyc = "myc2")

    fun `READ - Given a partner is stored When fetch all partners Then that partner is returned`() {
        save(partner)

        val partners = dao().fetchAll()

        assertThat(partners).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `CREATE - When insert partner Then expect partner to be inserted in database`() {
        dao().insert(partner)

        assertThat(fetchAll()).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `DELETE - Given one partner When delete him Then no partners exist`() {
        save(partner)

        dao().delete(partner)

        assertThat(fetchAll()).isEmpty()
    }

    fun `DELETE - Given two partners When delete single Then one should be still left`() {
        save(partner1, partner2)

        dao().delete(partner1)

        assertThat(fetchAll()).hasSize(1)
    }

    private fun save(vararg partners: PartnerDbo) {
        em.transactional {
            partners.forEach { partner ->
                persist(partner)
            }
        }
    }

    private fun fetchAll() =
            em.createQuery("SELECT p FROM PartnerDbo p", PartnerDbo::class.java).resultList

    private fun dao() = PartnerObjectDbDao(em)

}

fun PartnerDbo.Companion.testInstance() = PartnerDbo(
        id = 0L,
        idMyc = "testIdMyc",
        name = "testName"
)
