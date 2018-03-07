package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.domain.PartnerDao
import com.google.common.eventbus.EventBus
import javax.inject.Inject

interface PartnerService {
    fun create(partner: Partner): Partner
    fun readAll(): List<Partner>
    fun read(id: Long): Partner?
    fun findByShortName(shortName: String): Partner?
    fun findByShortNameOrThrow(shortName: String): Partner
    fun update(partner: Partner)
    fun searchPartner(locationHtml: String): Partner?
}

class PartnerServiceImpl @Inject constructor(
    private val partnerDao: PartnerDao,
    private val bus: EventBus
) : PartnerService {

    val log = LOG {}

    override fun create(partner: Partner): Partner {
        log.trace { "create(partner=$partner)" }
        return partnerDao.create(partner.toPartnerDbo()).toPartner()
    }

    override fun readAll(): List<Partner> {
        log.trace { "readAll()" }
        return partnerDao.readAll(includeIgnored = false).map { it.toPartner() }
    }

    override fun read(id: Long): Partner? {
        log.trace { "read(id=$id)" }
        return partnerDao.read(id)?.toPartner()
    }

    override fun findByShortName(shortName: String): Partner? {
        log.trace { "findByShortName(shortName=$shortName)" }
        return partnerDao.findByShortName(shortName)?.toPartner()
    }

    override fun findByShortNameOrThrow(shortName: String): Partner {
        log.trace { "findByShortNameOrThrow(shortName=$shortName)" }
        return findByShortName(shortName)
                ?: throw IllegalArgumentException("Could not find partner by short name '$shortName'!")
    }

    override fun update(partner: Partner) {
        log.trace { "update(partner=$partner)" }
        val updated = partnerDao.update(partner.toPartnerDbo())
        bus.post(PartnerUpdatedEvent(updated.toPartner()))
    }

    override fun searchPartner(locationHtml: String): Partner? {
        // MINOR performance improvement: do it directly in DB
        log.trace { "searchPartner(locationHtml=$locationHtml)" }
        val partners = partnerDao.readAll(includeIgnored = true).associateBy { it.name + "<br>" + it.address }
        return partners[locationHtml]?.toPartner()

    }

}
