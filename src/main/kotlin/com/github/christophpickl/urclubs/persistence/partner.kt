package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.inject.Inject
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

interface PartnerDao {
    fun create(partner: PartnerDbo): PartnerDbo
    fun readAll(): List<PartnerDbo>
    fun read(id: Long): PartnerDbo?
    fun delete(partner: PartnerDbo)
    fun update(partner: PartnerDbo)
}

class PartnerObjectDbDao @Inject constructor(
        private val em: EntityManager
) : PartnerDao {
    private val log = LOG {}

    override fun create(partner: PartnerDbo): PartnerDbo {
        log.debug { "create(partner=$partner)" }
        partner.ensureNotPersisted()
        em.transactional { persist(partner) }
        return partner
    }

    override fun readAll(): List<PartnerDbo> {
        log.debug { "readAll()" }
        val query = em.createQuery("SELECT p FROM ${PartnerDbo::class.simpleName} p", PartnerDbo::class.java)
        return query.resultList
    }

    override fun read(id: Long): PartnerDbo? =
            em.find(PartnerDbo::class.java, id)

    override fun update(partner: PartnerDbo) {
        log.debug { "update(partner=$partner)" }
        partner.ensurePersisted()
        em.transactional {
            val persisted = readOrThrow(partner.id)
            persisted.updateBy(partner)
        }
    }

    override fun delete(partner: PartnerDbo) {
        log.debug { "delete(partner=$partner)" }
        partner.ensurePersisted()
        em.transactional { remove(partner) }
    }

    private fun readOrThrow(id: Long) =
            read(id) ?: throw Exception("Partner not found by ID: $id")

}

@Entity
data class PartnerDbo(
        @Id @GeneratedValue(strategy = IDENTITY)
        override val id: Long,

        @Column(nullable = false, unique = true)
        var idMyc: String, // TODO change to val

        @Column(nullable = false, unique = true)
        var name: String,

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        var rating: RatingDbo

) : HasId {
    companion object

    fun updateBy(other: PartnerDbo) {
        if (name != other.name) name = other.name
        if (rating != other.rating) rating = other.rating
    }
}

enum class RatingDbo {
    UNKNOWN,
    BAD,
    OK,
    GOOD,
    SUPERB;
}
