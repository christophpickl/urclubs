package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.HasId
import com.github.christophpickl.urclubs.persistence.ensureNotPersisted
import com.github.christophpickl.urclubs.persistence.ensurePersisted
import com.github.christophpickl.urclubs.persistence.transactional
import javax.inject.Inject
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

interface PartnerDao {
    fun create(partner: PartnerDbo): PartnerDbo
    fun readAll(): List<PartnerDbo>
    fun read(id: Long): PartnerDbo?
    fun findByShortName(shortName: String): PartnerDbo?
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
        val query = em.createQuery("SELECT p FROM ${PartnerDbo::class.simpleName} p WHERE ${PartnerDbo::deletedByMyc.name} = false", PartnerDbo::class.java)
        return query.resultList
    }

    override fun read(id: Long): PartnerDbo? =
        em.find(PartnerDbo::class.java, id)

    override fun findByShortName(shortName: String): PartnerDbo? {
        val query = em.createQuery("SELECT p FROM ${PartnerDbo::class.simpleName} p WHERE p.shortName = :shortName", PartnerDbo::class.java)
        query.setParameter("shortName", shortName)
        val result = query.resultList
        return if (result.isEmpty()) null else result[0]
    }

    override fun update(partner: PartnerDbo) {
        log.debug { "update(partner=$partner)" }
        partner.ensurePersisted()
        em.transactional {
            val persisted = readOrThrow(partner.id)
            persisted.updateBy(partner)
        }
    }

    private fun readOrThrow(id: Long) =
        read(id) ?: throw Exception("Partner not found by ID: $id")

}

@Entity
data class PartnerDbo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long,

    @Column(nullable = false, unique = true)
    var idMyc: String = "",

    @Column(nullable = false, unique = false)
    var name: String = "",

    @Column(nullable = false, unique = true)
    var shortName: String = "",

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var rating: RatingDbo = RatingDbo.UNKNOWN,

    @Column(nullable = false)
    var deletedByMyc: Boolean = false,

    @Column(nullable = false)
    var favourited: Boolean = false,

    @Column(nullable = false)
    var wishlisted: Boolean = false,

    @Column(nullable = false)
    var ignored: Boolean = false,

    @Column(nullable = false)
    var category: CategoryDbo = CategoryDbo.UNKNOWN,

    @Column(nullable = false)
    var linkMyclubsSite: String = "",

    @Column(nullable = false)
    var linkPartnerSite: String = ""

) : HasId {
    companion object

    fun updateBy(other: PartnerDbo) {
        if (name != other.name) name = other.name
        if (shortName != other.shortName) shortName = other.shortName
        if (rating != other.rating) rating = other.rating
    }
}

enum class RatingDbo {
    UNKNOWN,
    BAD,
    OK,
    GOOD,
    SUPERB
}

enum class CategoryDbo {
    EMS,
    GYM,
    YOGA,
    WUSHU,
    WORKOUT,
    HEALTH,
    OTHER,
    UNKNOWN
}
