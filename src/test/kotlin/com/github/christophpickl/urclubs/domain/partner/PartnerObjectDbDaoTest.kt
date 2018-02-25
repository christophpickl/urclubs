package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerObjectDbDao
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo
import com.github.christophpickl.urclubs.persistence.transactional
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

    fun `READ - Given deleted partner When fetch all partners Then nothing is returned`() {
        save(partner.copy(deletedByMyc = true))

        val partners = dao().readAll()

        assertThat(partners).isEmpty()
    }

    fun `READ - Given partner When find by short name Then find`() {
        save(partner.copy(shortName = "foo"))

        val found = dao().findByShortName("foo")

        assertThat(found).isNotNull()
    }

    fun `READ - Given partner When find by wrong short name Then return null`() {
        save(partner.copy(shortName = "foo"))

        val found = dao().findByShortName("wrong")

        assertThat(found).isNull()
    }

    fun `UPDATE - Given saved partner When update that partner Then database contains updated partner`() {
        val savedPartner = partner.copy(name = "name1", rating = RatingDbo.UNKNOWN)
        save(savedPartner)
        val updatedPartner = savedPartner.copy(name = "name2", rating = RatingDbo.SUPERB)

        dao().update(updatedPartner)

        assertThatSingleElement(fetchAll(), updatedPartner)
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
