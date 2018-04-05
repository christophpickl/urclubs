package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.persistence.transactional
import com.github.christophpickl.urclubs.service.sync.testInstance
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.assertThatSingleElement
import com.github.christophpickl.urclubs.testInfra.singleEntryIsEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test

@Test
class PartnerDaoImplTest : DatabaseTest() {

    // need to be lateinit vars, as hibernate is going to mess around with those (actually imutable) objects
    private lateinit var partner: PartnerDbo
    private lateinit var partner1: PartnerDbo
    private lateinit var partner2: PartnerDbo
    private lateinit var partnerWithFinishedActivities: PartnerDbo

    @BeforeMethod
    fun createData() {
        partner = PartnerDbo.testInstance()
        partner1 = PartnerDbo.testInstance().copy(idMyc = "myc1")
        partner2 = PartnerDbo.testInstance().copy(idMyc = "myc2")
        partnerWithFinishedActivities = partner.copy(finishedActivities = mutableListOf(FinishedActivityDbo.testInstance()))
    }

    fun `CREATE - When insert partner Then expect partner to be inserted in database`() {
        dao().create(partner)

        assertThat(fetchAll()).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `CREATE - When insert partner with finished activities Then expect activities to be inserted in database`() {
        dao().create(partnerWithFinishedActivities)

        assertThat(fetchAll()).singleEntryIsEqualToIgnoringGivenProps(partnerWithFinishedActivities, PartnerDbo::id)
    }

    fun `READ - Given a partner is stored When fetch all partners Then that partner is returned`() {
        save(partner)

        val partners = dao().readAll(includeIgnored = true)

        assertThat(partners).singleEntryIsEqualToIgnoringGivenProps(partner, PartnerDbo::id)
    }

    fun `READ - Given deleted partner When fetch all partners Then nothing is returned`() {
        save(partner.copy(deletedByMyc = true))

        val partners = dao().readAll(includeIgnored = true)

        assertThat(partners).isEmpty()
    }

    fun `READ - Given ignored partner When fetch all but ignored partners Then nothing is returned`() {
        save(partner.copy(ignored = true))

        val partners = dao().readAll(includeIgnored = false)

        assertThat(partners).isEmpty()
    }

    fun `READ - Given ignored partner When fetch all with ignored partners Then one partner is returned`() {
        save(partner.copy(ignored = true))

        val partners = dao().readAll(includeIgnored = true)

        assertThat(partners).hasSize(1)
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

    fun `READ - Given partner with 3 addresses When search by name and address Then return not null`() {
        val partner = save(partner.copy(name = "name", addresses = mutableListOf("address1", "address2", "address3")))

        val found = dao().searchByNameAndAddress(partner.name, partner.addresses[1])

        assertThat(found).isNotNull()
    }

    fun `READ - Given partner When search by correct name and incorrect address Then return null`() {
        val partner = save(partner.copy(name = "name", addresses = mutableListOf("address")))

        val found = dao().searchByNameAndAddress(partner.name, "incorrect")

        assertThat(found).isNull()
    }

    fun `READ - Given partner When search by incorrect name and correct address Then return null`() {
        val partner = save(partner.copy(name = "name", addresses = mutableListOf("address")))

        val found = dao().searchByNameAndAddress("incorrect", partner.addresses[0])

        assertThat(found).isNull()
    }

    fun `READ - Given empty DB When search by name and address Then return null`() {
        val found = dao().searchByNameAndAddress("anyName", "anyAddress")

        assertThat(found).isNull()
    }

    fun `UPDATE - Given saved partner When update that partner Then database contains updated partner`() {
        val savedPartner = PartnerDbo(
            name = "name",
            note = "note",
            rating = RatingDbo.GOOD,
            category = CategoryDbo.GYM,
            deletedByMyc = true,
            favourited = true,
            wishlisted = true,
            ignored = true,
            maxCredits = 1,

            id = 0L,
            idMyc = "idMyc",
            shortName = "shortName",
            linkPartner = "linkPartner",
            linkMyclubs = "linkMyclubs",
            picture = null,
            addresses = mutableListOf("address"),
            tags = mutableListOf("tag", "willBeRemoved"),
            finishedActivities = mutableListOf()
        )
        save(savedPartner)
        val updatedPartner = PartnerDbo(
            name = "name2",
            note = "note2",
            rating = RatingDbo.BAD,
            category = CategoryDbo.EMS,
            deletedByMyc = false,
            favourited = false,
            wishlisted = false,
            ignored = false,
            maxCredits = 2,

            id = savedPartner.id,
            idMyc = savedPartner.idMyc,
            shortName = savedPartner.shortName,
            addresses = mutableListOf("address", "wasAdded"),
            tags = mutableListOf("tag"),
            linkPartner = savedPartner.linkPartner,
            linkMyclubs = savedPartner.linkMyclubs,
            picture = ByteArray(8, { 1 }),
            finishedActivities = mutableListOf(FinishedActivityDbo.testInstance())
        )

        dao().update(updatedPartner)

        assertThatSingleElement(fetchAll(), updatedPartner)
    }

    private fun save(partner: PartnerDbo): PartnerDbo {
        em.transactional {
            persist(partner)
        }
        return partner
    }

    private fun fetchAll() =
        em.createQuery("SELECT p FROM PartnerDbo p", PartnerDbo::class.java).resultList

    private fun dao() = PartnerDaoImpl(em)

}
