package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.urclubs.testInfra.singleEntryIsEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.io.File
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

@Test
class PartnerObjectDbDaoTest {
    private lateinit var emFactory: EntityManagerFactory
    private lateinit var em: EntityManager
    private val dbPath = "build/test_db.odb"

    private val partner = PartnerDbo.testInstance()

    @BeforeMethod
    fun setupDb() {
        emFactory = Persistence.createEntityManagerFactory(dbPath)
        em = emFactory.createEntityManager()
    }

    @AfterMethod
    fun tearDownDb() {
        em.close()
        emFactory.close()
        File(dbPath).delete()
    }

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
