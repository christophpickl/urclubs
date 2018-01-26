package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import javax.inject.Inject

class PartnerSyncer @Inject constructor(
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService
) {

    private val log = LOG {}

    fun sync(): PartnerSyncReport {
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

        return PartnerSyncReport(
                insertedPartners = insertedPartners,
                deletedPartners = deletedPartners
        )
    }
}

data class PartnerSyncReport(
        val insertedPartners: List<Partner>,
        val deletedPartners: List<Partner>
) {
    fun toPrettyString() =
            """Sync Report:
==========================
Inserted (${insertedPartners.size}):
--------------------------
${insertedPartners.toPrettyString()}

Deleted(${deletedPartners.size}):
--------------------------
${deletedPartners.toPrettyString()}
"""

}

fun PartnerHtmlModel.toPartner() = Partner(
        idDbo = 0L,
        idMyc = id,
        name = name,
        shortName = shortName,
        rating = Rating.UNKNOWN
)
