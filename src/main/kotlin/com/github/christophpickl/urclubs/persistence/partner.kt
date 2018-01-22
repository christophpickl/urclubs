package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.inject.Inject
import javax.persistence.Entity
import javax.persistence.EntityManager
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id

interface PartnerDao {
    fun insert(partner: PartnerDbo)
    fun fetchAll(): List<PartnerDbo>
}

class PartnerObjectDbDao @Inject constructor(
        private val em: EntityManager
) : PartnerDao {

    private val log = LOG {}

    override fun insert(partner: PartnerDbo) {
        log.debug { "insert(partner=$partner)" }
        partner.ensureNotPersisted()
        em.transactional { em.persist(partner) }
    }

    override fun fetchAll(): List<PartnerDbo> {
        log.debug { "fetchAll()" }
        val query = em.createQuery("SELECT p FROM ${PartnerDbo::class.simpleName} p", PartnerDbo::class.java)
        return query.resultList
    }

}

@Entity
data class PartnerDbo(

        @Id @GeneratedValue(strategy = IDENTITY)
        override val id: Long,

        var name: String

): HasId {

    companion object
}
