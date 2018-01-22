package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.Partner
import com.github.christophpickl.urclubs.persistence.PartnerDao
import com.github.christophpickl.urclubs.persistence.PartnerDbo
import javax.inject.Inject

interface PartnerService {
    fun insert(partner: Partner): Partner
    fun fetchAll(): List<Partner>
    fun delete(partner: Partner)
}

class PartnerServiceImpl @Inject constructor(
        private val partnerDao: PartnerDao
): PartnerService {

    override fun insert(partner: Partner) =
            partnerDao.insert(partner.toPartnerDbo()).toPartner()

    override fun fetchAll() =
            partnerDao.fetchAll().map { it.toPartner() }

    override fun delete(partner: Partner) {
        partnerDao.delete(partner.toPartnerDbo())
    }

}

fun Partner.toPartnerDbo() = PartnerDbo(
        id = idDbo,
        idMyc = idMyc,
        name = name
)

fun PartnerDbo.toPartner() = Partner(
        idDbo = id,
        idMyc = idMyc,
        name = name
)
