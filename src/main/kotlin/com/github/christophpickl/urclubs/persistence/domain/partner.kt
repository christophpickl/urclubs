package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.kpotpourri.common.arrays.byteArrayEquals
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_BIG
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_LIL
import com.github.christophpickl.urclubs.persistence.COL_LENGTH_MED
import com.github.christophpickl.urclubs.persistence.HasId
import com.github.christophpickl.urclubs.persistence.ONE_MB
import com.github.christophpickl.urclubs.persistence.deleteAll
import com.github.christophpickl.urclubs.persistence.ensureNotPersisted
import com.github.christophpickl.urclubs.persistence.ensurePersisted
import com.github.christophpickl.urclubs.persistence.persistAndReturn
import com.github.christophpickl.urclubs.persistence.queryList
import com.github.christophpickl.urclubs.persistence.transactional
import com.google.common.base.MoreObjects
import com.google.common.base.Objects
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

fun EntityManager.deleteAllPartners() {
    createNativeQuery("DELETE FROM PartnerDbo_addresses").executeUpdate()
    createNativeQuery("DELETE FROM PartnerDbo_finishedActivities").executeUpdate()
    deleteAll<PartnerDbo>()
}

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

        return em.transactional {
            persistAndReturn(persisted)
        }
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

        @ElementCollection
        var tags: MutableList<String>,

        @ElementCollection
        var addresses: MutableList<String>,

        @ElementCollection
        var finishedActivities: MutableList<FinishedActivityDbo>,

        @Lob
        @Column(nullable = true, length = ONE_MB)
        var picture: ByteArray?

        // dont forget to extend the equals() method when adding new properties!

) : HasId {
    companion object {
        val MAX_PICTURE_BYTES = 1024 * 1024 // == 1MB
    }

    override fun equals(other: Any?): Boolean {
        // @formatter:off
        if (other !is PartnerDbo)                                             return false
        if (id           != other.id)                                         return false
        if (idMyc        != other.idMyc)                                      return false
        if (name         != other.name)                                       return false
        if (shortName    != other.shortName)                                  return false
        if (note         != other.note)                                       return false
        if (linkMyclubs  != other.linkMyclubs)                                return false
        if (linkPartner  != other.linkPartner)                                return false
        if (maxCredits   != other.maxCredits)                                 return false
        if (rating       != other.rating)                                     return false
        if (category     != other.category)                                   return false
        if (deletedByMyc != other.deletedByMyc)                               return false
        if (favourited   != other.favourited)                                 return false
        if (wishlisted   != other.wishlisted)                                 return false
        if (ignored      != other.ignored)                                    return false
        if (!picture.byteArrayEquals(other.picture))                          return false
        // hibernate bag is messing up the equals method
        if (addresses.toList()          != other.addresses.toList())          return false
        if (tags.toList()               != other.tags.toList())               return false
        if (finishedActivities.toList() != other.finishedActivities.toList()) return false
        // @formatter:on
        return true
    }

    fun updateBy(other: PartnerDbo) {
        // @formatter:off
        if (name         != other.name)         name         = other.name
        if (shortName    != other.shortName)    shortName    = other.shortName
        if (rating       != other.rating)       rating       = other.rating
        if (category     != other.category)     category     = other.category
        if (maxCredits   != other.maxCredits)   maxCredits   = other.maxCredits
        if (note         != other.note)         note         = other.note
        if (favourited   != other.favourited)   favourited   = other.favourited
        if (wishlisted   != other.wishlisted)   wishlisted   = other.wishlisted
        if (deletedByMyc != other.deletedByMyc) deletedByMyc = other.deletedByMyc
        if (ignored      != other.ignored)      ignored      = other.ignored
        addresses.updateByIfNeeded(other.addresses)
        tags.updateByIfNeeded(other.tags)
        finishedActivities.updateByIfNeeded(other.finishedActivities)
        if (!picture.byteArrayEquals(other.picture)) picture = other.picture
        // @formatter:on
    }

    private fun <T> MutableList<T>.updateByIfNeeded(other: List<T>) {
        if (this != other) {
            clear()
            addAll(other)
        }
    }

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("id", id)
            .add("idMyc", idMyc)
            .add("shortName", shortName)
            .add("rating", rating)
            .add("category", category)
            .add("maxCredits", maxCredits)
            .add("favourited", favourited)
            .add("wishlisted", wishlisted)
            .add("addresses", addresses)
            .add("tags", tags)
            .add("finishedActivities.size", finishedActivities.size)
            .add("picture-set", picture != null)
            .toString()

    override fun hashCode() = Objects.hashCode(id, name, shortName)

}


enum class RatingDbo {
    UNKNOWN,

    BAD,
    OK,
    GOOD,
    SUPERB
}

enum class CategoryDbo {
    UNKNOWN,

    DANCE,
    EMS,
    GYM,
    YOGA,
    WUSHU,
    WORKOUT,
    HEALTH,
    OTHER,
    PILATES,
    WATER,
}
