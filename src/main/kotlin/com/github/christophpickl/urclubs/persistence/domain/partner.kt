package com.github.christophpickl.urclubs.persistence.domain

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
    fun readAll(includeIgnored: Boolean?): List<PartnerDbo>
    fun read(id: Long): PartnerDbo?
    fun findByShortName(shortName: String): PartnerDbo?
    fun update(partner: PartnerDbo): PartnerDbo
}

class PartnerDaoImpl @Inject constructor(
    private val em: EntityManager
) : PartnerDao {
    private val log = LOG {}

    override fun create(partner: PartnerDbo): PartnerDbo {
        log.debug { "create(partner=$partner)" }
        partner.ensureNotPersisted()
        em.transactional { persist(partner) }
        return partner
    }

    override fun readAll(includeIgnored: Boolean?): List<PartnerDbo> {
        log.debug { "readAll()" }
        val whereIgnored = if (includeIgnored == null) "" else " AND ${PartnerDbo::ignored.name} = $includeIgnored"
        val query = em.createQuery("SELECT p FROM ${PartnerDbo::class.simpleName} p WHERE ${PartnerDbo::deletedByMyc.name} = false" + whereIgnored, PartnerDbo::class.java)
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

    override fun update(partner: PartnerDbo): PartnerDbo {
        log.debug { "update(partner=$partner)" }
        partner.ensurePersisted()

        val persisted = readOrThrow(partner.id)
        persisted.updateBy(partner)
        em.transactional {
            persist(persisted)
        }
        return persisted
    }

    private fun readOrThrow(id: Long) =
        read(id) ?: throw Exception("Partner not found by ID: $id")

}

const val COL_LENGTH_LIL = 128
const val COL_LENGTH_MED = 512
const val COL_LENGTH_BIG = 5120

@Entity
data class PartnerDbo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    override val id: Long,

    @Column(nullable = false, length = COL_LENGTH_MED, unique = true)
    var idMyc: String,

    @Column(nullable = false, length = COL_LENGTH_MED, unique = false)
    var name: String,

    @Column(nullable = false, length = COL_LENGTH_MED, unique = true)
    var shortName: String,

    @Column(nullable = false, length = COL_LENGTH_MED)
    var address: String,

    @Column(nullable = false, length = COL_LENGTH_BIG)
    var note: String,

    @Column(nullable = false, length = COL_LENGTH_LIL)
    var linkMyclubsSite: String,

    @Column(nullable = false, length = COL_LENGTH_LIL)
    var linkPartnerSite: String,

    @Column(nullable = false, length = COL_LENGTH_LIL)
    @Enumerated(EnumType.STRING)
    var rating: RatingDbo,

    @Column(nullable = false)
    var category: CategoryDbo,

    @Column(nullable = false)
    var deletedByMyc: Boolean,

    @Column(nullable = false)
    var favourited: Boolean,

    @Column(nullable = false)
    var wishlisted: Boolean,

    @Column(nullable = false)
    var ignored: Boolean

) : HasId {
    companion object

    fun updateBy(other: PartnerDbo) {
        if (name != other.name) name = other.name
        if (shortName != other.shortName) shortName = other.shortName
        if (rating != other.rating) rating = other.rating
        if (category != other.category) category = other.category
        if (note != other.note) note = other.note
        if (deletedByMyc != other.deletedByMyc) deletedByMyc = other.deletedByMyc
        if (favourited != other.favourited) favourited = other.favourited
        if (wishlisted != other.wishlisted) wishlisted = other.wishlisted
        if (ignored != other.ignored) ignored = other.ignored
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
