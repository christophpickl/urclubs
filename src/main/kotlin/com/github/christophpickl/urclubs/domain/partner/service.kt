package com.github.christophpickl.urclubs.domain.partner

import javax.inject.Inject

interface PartnerService {
    fun create(partner: Partner): Partner
    fun readAll(): List<Partner>
    fun read(id: Long): Partner?
    fun findByShortName(shortName: String): Partner?
    fun findByShortNameOrThrow(shortName: String): Partner
    fun update(partner: Partner)
    fun delete(partner: Partner)
}

class PartnerServiceImpl @Inject constructor(
        private val partnerDao: PartnerDao
) : PartnerService {

    override fun create(partner: Partner) =
            partnerDao.create(partner.toPartnerDbo()).toPartner()

    override fun readAll() =
            partnerDao.readAll().map { it.toPartner() }

    override fun read(id: Long) =
            partnerDao.read(id)?.toPartner()

    override fun findByShortName(shortName: String) =
            partnerDao.findByShortName(shortName)?.toPartner()

    override fun findByShortNameOrThrow(shortName: String) =
            findByShortName(shortName) ?: throw IllegalArgumentException("Could not find partner by short name '$shortName'!")

    override fun update(partner: Partner) {
        partnerDao.update(partner.toPartnerDbo())
    }

    override fun delete(partner: Partner) {
        partnerDao.delete(partner.toPartnerDbo())
    }
}
