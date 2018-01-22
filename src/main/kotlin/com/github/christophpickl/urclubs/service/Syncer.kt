package com.github.christophpickl.urclubs.service

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.Partner
import com.github.christophpickl.urclubs.Rating
import com.github.christophpickl.urclubs.backend.MyClubsApi
import com.github.christophpickl.urclubs.backend.PartnerMyc
import javax.inject.Inject

class Syncer @Inject constructor(
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService
) {

    private val log = LOG {}

    fun sync(): SyncerReport {
        log.info { "sync()" }
        val partnersMyc = myclubs.partners()
        val partnersDbo = partnerService.readAll()

        val mycsById = partnersMyc.associateBy { it.id }
        val idOfMycs = mycsById.keys
        val dbosByIdMyc = partnersDbo.associateBy { it.idMyc }
        val idMycOfDbos = dbosByIdMyc.keys

        val insertedPartners = mycsById.minus(idMycOfDbos).values.map { partnerMyc ->
            partnerService.create(partnerMyc.toPartner())
        }

        val deletedPartners = dbosByIdMyc.minus(idOfMycs).values.apply {
            forEach {
                partnerService.delete(it)
            }
        }.toList()

        return SyncerReport(
                insertedPartners = insertedPartners,
                deletedPartners = deletedPartners
        )
    }
}

data class SyncerReport(
        val insertedPartners: List<Partner>,
        val deletedPartners: List<Partner>
)

fun PartnerMyc.toPartner() = Partner(
        idDbo = 0L,
        idMyc = id,
        name = title,
        rating = Rating.UNKNOWN
)
