package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.UpcomingActivityDao
import java.time.LocalDateTime
import javax.inject.Inject
import javax.persistence.EntityManager

interface ActivityService {

    // save is done via Partner.finishedActivities
    fun readAllFinished(): List<FinishedActivity>
    fun readUpcoming(start: LocalDateTime, end: LocalDateTime): List<UpcomingActivity>
    // fun readFutureUpcomingFor(partner: PartnerDbo): List<UpcomingActivity>
    fun createUpcoming(activities: List<UpcomingActivity>): List<UpcomingActivity>
}

class ActivityServiceImpl @Inject constructor(
    private val em: EntityManager,
    private val upcomingDao: UpcomingActivityDao
) : ActivityService {

    private val log = LOG {}

    override fun readAllFinished(): List<FinishedActivity> {
        log.debug { "readAllFinished()" }

        val builder = em.criteriaBuilder
        val criteria = builder.createQuery(PartnerDbo::class.java).apply {
            val root = from(PartnerDbo::class.java)
            select(root)
        }
        val partners = em.createQuery(criteria).resultList
        return partners.flatMap { it.finishedActivities }.map { it.toFinishedActivity() }
    }

    override fun readUpcoming(start: LocalDateTime, end: LocalDateTime): List<UpcomingActivity> {
        return upcomingDao.read(start, end).map { it.toUpcomingActivity() }
    }

    override fun createUpcoming(activities: List<UpcomingActivity>): List<UpcomingActivity> {
        // TODO !!! somehow match activity DBO with partner DBO
//        return upcomingDao.create(activities.map { it.toUpcomingActivityDbo() }).map { it.toUpcomingActivity() }
        return emptyList()
    }

}
