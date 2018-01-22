package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import com.github.christophpickl.urclubs.testInfra.singleEntryIsEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class PartnerObjectDbDaoTest : DatabaseTest() {

    private lateinit var partner: PartnerDbo
    private lateinit var partner1: PartnerDbo
    private lateinit var partner2: PartnerDbo

    @BeforeMethod
    fun createData() {
        partner = PartnerDbo.testInstance()
        partner1 = PartnerDbo.testInstance().copy(idMyc = "myc1")
        partner2 = PartnerDbo.testInstance().copy(idMyc = "myc2")
    }

    fun `CREATE - When insert partner Then expect partner to be inserted in database`() {
        dao().create(partner)

        assertThat(fetchAll()).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `READ - Given a partner is stored When fetch all partners Then that partner is returned`() {
        save(partner)

        val partners = dao().readAll()

        assertThat(partners).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `UPDATE - Given saved partner When update that partner Then database contains updated partner`() {
        val savedPartner = partner.copy(name = "name1", rating = RatingDbo.UNKNOWN)
        save(savedPartner)
        val updatedPartner = savedPartner.copy(name = "name2", rating = RatingDbo.SUPERB)

        dao().update(updatedPartner)

        assertThatSingleElement(fetchAll(), updatedPartner)
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
        name = "testName",
        rating = RatingDbo.UNKNOWN
)
