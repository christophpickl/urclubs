package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.Partner
import com.github.christophpickl.urclubs.persistence.PartnerDao
import com.github.christophpickl.urclubs.persistence.PartnerDbo
import javax.inject.Inject

class PartnerService @Inject constructor(
        private val partnerDao: PartnerDao
) {

    fun insert(partner: Partner) {
        partnerDao.insert(partner.toPartnerDbo())
    }

    fun fetchAll() = partnerDao.fetchAll().map { it.toPartner() }

}

fun Partner.toPartnerDbo() = PartnerDbo(
        id = idDbo,
        name = name
)

fun PartnerDbo.toPartner() = Partner(
        idDbo = id,
        name = name
)
