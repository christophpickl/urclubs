package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_MED
import com.github.christophpickl.urclubs.persistence.HasId
import com.github.christophpickl.urclubs.persistence.transactional
import java.time.LocalDateTime
import javax.inject.Inject
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Embeddable
data class FinishedActivityDbo(

    @Column(nullable = false, length = COL_LENGTH_MED)
    var title: String,

    @Column(nullable = false)
    var date: LocalDateTime

) {
    companion object
}

@Entity
data class UpcomingActivityDbo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partnerId")
    var partner: PartnerDbo,

    @Column(nullable = false, length = COL_LENGTH_MED)
    var title: String,

    @Column(nullable = false)
    var date: LocalDateTime

) : HasId {
    companion object
}

interface UpcomingActivityDao {
    fun read(start: LocalDateTime, end: LocalDateTime): List<UpcomingActivityDbo>
    fun create(activities: List<UpcomingActivityDbo>): List<UpcomingActivityDbo>
}

class UpcomingActivityDaoImpl @Inject constructor(
    private val em: EntityManager
) : UpcomingActivityDao {

    private val log = LOG {}

    override fun read(start: LocalDateTime, end: LocalDateTime): List<UpcomingActivityDbo> {
        log.debug { "read(start=$start, end=$end)" }
        val builder = em.criteriaBuilder
        val criteria = builder.createQuery(UpcomingActivityDbo::class.java).apply {
            val root = from(UpcomingActivityDbo::class.java)
            select(root)
            var where = builder.conjunction()
            where = builder.and(where, builder.between(root.get<LocalDateTime>(UpcomingActivityDbo::date.name), builder.literal(start), builder.literal(end)))
            where(where)
        }
        return em.createQuery(criteria).resultList
    }

    override fun create(activities: List<UpcomingActivityDbo>): List<UpcomingActivityDbo> {
        if (activities.isEmpty()) {
            log.warn { "create(activities) ... given empty list :-/" }
            return emptyList()
        }
        log.debug { "create(activities.size=${activities.size})" }
        return em.transactional {
            activities.map { activity ->
                // activity.ensureNotPersisted()
                persist(activity)
                activity
            }
        }
    }

}
