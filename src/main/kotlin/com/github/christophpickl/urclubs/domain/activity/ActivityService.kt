package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.domain.FinishedActivityDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import javax.inject.Inject
import javax.persistence.EntityManager

interface ActivityService {

    fun readAllFinished(): List<FinishedActivity>
    // save is done via Partner.finishedActivities
}

class ActivityServiceImpl @Inject constructor(
    private val em: EntityManager
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

}
