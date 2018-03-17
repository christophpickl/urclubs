package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.byteArrayEquals
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_BIG
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_LIL
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_MED
import com.github.christophpickl.urclubs.persistence.HasId
import com.github.christophpickl.urclubs.persistence.ONE_MB
import com.github.christophpickl.urclubs.persistence.ensureNotPersisted
import com.github.christophpickl.urclubs.persistence.ensurePersisted
import com.github.christophpickl.urclubs.persistence.queryList
import com.github.christophpickl.urclubs.persistence.transactional
import com.google.common.base.MoreObjects
import javax.inject.Inject
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Lob

interface PartnerDao {
    fun create(partner: PartnerDbo): PartnerDbo
    fun readAll(includeIgnored: Boolean): List<PartnerDbo>
    fun read(id: Long): PartnerDbo?
    fun findByShortName(shortName: String): PartnerDbo?
    fun update(partner: PartnerDbo): PartnerDbo
    fun searchByNameAndAddress(name: String, address: String): PartnerDbo?
}

class PartnerDaoImpl @Inject constructor(
    private val em: EntityManager
) : PartnerDao {

    override fun searchByNameAndAddress(name: String, address: String): PartnerDbo? {
        val builder = em.criteriaBuilder
        val criteria = builder.createQuery(PartnerDbo::class.java).apply {
            val root = from(PartnerDbo::class.java)
            select(root)
            var where = builder.conjunction()
            where = builder.and(where, builder.equal(root.get<PartnerDbo>(PartnerDbo::name.name), name))
            where = builder.and(where, builder.isMember(address, root.get(PartnerDbo::addresses.name)))
            where(where)
        }
        return em.createQuery(criteria).resultList.firstOrNull()
    }

    private val log = LOG {}

    override fun create(partner: PartnerDbo): PartnerDbo {
        log.debug { "create(partner=$partner)" }
        partner.ensureNotPersisted()
        em.transactional { persist(partner) }
        return partner
    }

    override fun readAll(includeIgnored: Boolean): List<PartnerDbo> {
        log.debug { "readAll(includeIgnored=$includeIgnored)" }
        val whereIgnored = if (includeIgnored) "" else " AND ${PartnerDbo::ignored.name} = $includeIgnored"
        return em.queryList("SELECT p FROM ${PartnerDbo::class.simpleName} p WHERE ${PartnerDbo::deletedByMyc.name} = false" + whereIgnored)
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

    @ElementCollection
    var addresses: List<String>,

    @Column(nullable = false, length = COL_LENGTH_BIG)
    var note: String,

    @Column(nullable = false, length = COL_LENGTH_LIL)
    var linkMyclubs: String,

    @Column(nullable = false, length = COL_LENGTH_LIL)
    var linkPartner: String,

    @Column(nullable = false)
    var maxCredits: Byte,

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
    var ignored: Boolean,

    @Lob
    @Column(nullable = true, length = ONE_MB)
    var picture: ByteArray?

) : HasId {
    companion object {
        val MAX_PICTURE_BYTES = 1024 * 1024 // == 1MB
    }

    fun updateBy(other: PartnerDbo) {
        // @formatter:off
        if (name         != other.name)         name         = other.name
        if (shortName    != other.shortName)    shortName    = other.shortName
        if (rating       != other.rating)       rating       = other.rating
        if (category     != other.category)     category     = other.category
        if (maxCredits   != other.maxCredits)   maxCredits   = other.maxCredits
        if (note         != other.note)         note         = other.note
        if (deletedByMyc != other.deletedByMyc) deletedByMyc = other.deletedByMyc
        if (favourited   != other.favourited)   favourited   = other.favourited
        if (wishlisted   != other.wishlisted)   wishlisted   = other.wishlisted
        if (ignored      != other.ignored)      ignored      = other.ignored
        if (!picture.byteArrayEquals(other.picture)) picture = other.picture
        // @formatter:on
    }

    override fun toString() = MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("shortName", shortName)
        .add("idMyc", idMyc)
        .add("--picture-set", picture != null)
        .toString()
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
