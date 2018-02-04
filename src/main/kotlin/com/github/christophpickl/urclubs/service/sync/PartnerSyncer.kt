package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
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

        // TODO introduce coroutines for syncing each partner
        val insertedPartners = mycsById.minus(idMycOfDbos).values.map { partnerMyc ->
            val rawPartner = partnerMyc.toPartner()
            val partner = rawPartner.enhance(myclubs.partner(rawPartner.shortName))
            partnerService.create(partner)
        }

        val deletedPartners = dbosByIdMyc.minus(idOfMycs).values
                .map { it.copy(deletedByMyc = true) }
                .apply {
                    forEach {
                        partnerService.update(it)
                    }
                }.toList()

        return PartnerSyncReport(
                insertedPartners = insertedPartners,
                deletedPartners = deletedPartners
        )
    }
}

private fun Partner.enhance(detailed: PartnerDetailHtmlModel) = copy(
    address = detailed.address
    // description
    // link
    // flags
)

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
        address = "", // needs additional GET /partner request
        rating = Rating.UNKNOWN,
        deletedByMyc = false
)