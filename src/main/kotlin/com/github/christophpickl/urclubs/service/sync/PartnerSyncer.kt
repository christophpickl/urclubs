package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerImage
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.MyclubsUtil
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.PartnerHtmlModel
import com.github.christophpickl.urclubs.service.Clock
import java.time.LocalDateTime
import javax.inject.Inject

class PartnerSyncer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val partnerService: PartnerService,
    private val util: MyclubsUtil,
    private val clock: Clock
) {

    private val log = LOG {}

    fun sync(): PartnerSyncReport {
        log.info { "sync()" }
        val now = clock.now()

        val partnersFetched = myclubs.partners().let { if (UrclubsConfiguration.Development.FAST_SYNC) it.take(5) else it }
        val partnersStored = partnerService.readAll(includeIgnored = true)

        val fetchedById = partnersFetched.associateBy { it.id }
        val fetchedIds = fetchedById.keys
        val storedById = partnersStored.associateBy { it.idMyc }
        val storedIds = storedById.keys

        val insertedPartners = fetchedById.minus(storedIds).values.map { partnerMyc ->
            val rawPartner = partnerMyc.toPartner(dateInserted = now)
            val detailPartner = myclubs.partner(rawPartner.shortName)
            val partner = rawPartner.enhance(detailPartner)
            partnerService.create(partner)
        }

        val deletedPartners = storedById.minus(fetchedIds).values
            .map { it.copy(dateDeleted = now) }
            .apply {
                forEach {
                    log.trace { "Marking partner as deleted by myclubs: $it" }
                    partnerService.update(it)
                }
            }.toList()

        log.info { "sync() DONE" }
        return PartnerSyncReport(
            insertedPartners = insertedPartners,
            deletedPartners = deletedPartners
        )
    }

    private fun Partner.enhance(detailed: PartnerDetailHtmlModel) = copy(
        addresses = detailed.addresses,
        linkPartner = detailed.linkPartnerSite,
        linkMyclubs = util.createMyclubsPartnerUrl(shortName),
        tags = detailed.tags
    ).let {
        PartnerSyncerPostProcessor.process(it, detailed)
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

fun PartnerHtmlModel.toPartner(dateInserted: LocalDateTime) = Partner(
    idDbo = 0L,
    idMyc = id,
    name = name,
    note = "",
    shortName = shortName,
    rating = Rating.UNKNOWN,
    category = Category.UNKNOWN,
    maxCredits = Partner.DEFAULT_MAX_CREDITS,
    dateInserted = dateInserted,
    dateDeleted = null,
    favourited = false,
    wishlisted = false,
    ignored = false,
    picture = PartnerImage.DefaultPicture,

    // will be enhanced later on by HTTP detail request
    linkMyclubs = "",
    linkPartner = null,
    addresses = emptyList(),
    tags = emptyList(),
    finishedActivities = emptyList()
)
