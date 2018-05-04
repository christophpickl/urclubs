package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.urclubs.persistence.persistTransactional
import com.github.christophpickl.urclubs.service.sync.testInstance
import com.github.christophpickl.urclubs.testInfra.DatabaseTest
import com.github.christophpickl.urclubs.testInfra.isEqualToIgnoringGivenProps
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.hibernate.TransientPropertyValueException
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import java.time.LocalDateTime
import javax.persistence.EntityManager

@Test
class UpcomingActivityDaoImplTest : DatabaseTest() {

    private val dateDay1a = LocalDateTime.parse("2000-01-01T00:00:00")
    private val dateDay1b = LocalDateTime.parse("2000-01-01T23:00:00")

    private lateinit var savedPartner: PartnerDbo

    @BeforeMethod
    fun `setup saved partner`() {
        savedPartner = em.persistTransactional(PartnerDbo.testInstance.copy(id = 0L))
    }

    fun `Given no data When read Then return empty list `() {
        val actual = dao().read(dateDay1a, dateDay1b)

        assertThat(actual).isEmpty()
    }

    fun `Given an activity in range When read Then return that activity`() {
        val saved = saveActivity { copy(date = dateDay1a) }

        val actual = dao().read(dateDay1a, dateDay1b)

        assertThat(actual).containsExactly(saved)
    }

    fun `Given an activity out of range When read Then return empty list`() {
        saveActivity { copy(date = dateDay1a.minusDays(1)) }

        val actual = dao().read(dateDay1a, dateDay1b)

        assertThat(actual).isEmpty()
    }

    fun `Given no partner When create Then fail`() {
        assertThatThrownBy {
            dao().create(listOf(UpcomingActivityDbo.testInstance.copy(id = 0L, partner = PartnerDbo.testInstance)))
        }.hasRootCauseExactlyInstanceOf(TransientPropertyValueException::class.java)
    }

    fun `Given saved partner When create Then return and persisted`() {
        val given = UpcomingActivityDbo.testInstance.copy(id = 0L, partner = savedPartner)

        val saved = dao().create(listOf(given))

        assertThat(saved).hasSize(1)
        assertThat(saved[0]).isEqualToIgnoringGivenProps(given, UpcomingActivityDbo::id)

        assertThat(em.fetchUpcomingActivityDbo()).containsExactly(saved[0])
    }

    private fun saveActivity(with: UpcomingActivityDbo.() -> UpcomingActivityDbo = { this }): UpcomingActivityDbo {
        return em.persistTransactional(UpcomingActivityDbo.testInstance.copy(id = 0L, partner = savedPartner).let(with))
    }

    private fun dao() = UpcomingActivityDaoImpl(em)


}

fun EntityManager.fetchUpcomingActivityDbo(): List<UpcomingActivityDbo> =
    createQuery("SELECT a FROM UpcomingActivityDbo a", UpcomingActivityDbo::class.java).resultList
