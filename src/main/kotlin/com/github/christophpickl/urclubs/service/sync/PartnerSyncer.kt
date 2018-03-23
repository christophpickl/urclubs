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
import com.google.common.annotations.VisibleForTesting
import javax.inject.Inject

class PartnerSyncer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val partnerService: PartnerService,
    private val util: MyclubsUtil
) {

    private val log = LOG {}

    object PostProcessor {

        @VisibleForTesting
        fun process(partner: Partner, detailed: PartnerDetailHtmlModel): Partner {
            return partner.copy(
                category = processCategory(detailed),
                ignored = detailed.tags.contains("Exklusiv für Frauen")
            )
        }

        private fun processCategory(detailed: PartnerDetailHtmlModel): Category {
            val loweredTags = detailed.tags.map { it.toLowerCase() }
            if (loweredTags.anyContains("fitnessstudio")) return Category.GYM
            if (loweredTags.anyContains("kampfkunst")) return Category.WUSHU
            if (loweredTags.anyContains("yoga")) return Category.YOGA
            if (loweredTags.anyContains("fitnesskurse")) return Category.WORKOUT
            if (loweredTags.anyContains("ems")) return Category.EMS
            if (loweredTags.anyContains("boxen")) return Category.WUSHU
            if (loweredTags.anyContains("wassersport")) return Category.WATER
            if (loweredTags.anyContains("dance")) return Category.DANCE
            if (loweredTags.anyContains("pilates")) return Category.PILATES
            return Category.UNKNOWN
        }

        private fun List<String>.anyContains(search: String) = any { it.contains(search) }
    }

    fun sync(): PartnerSyncReport {
        log.info { "sync()" }

        val partnersFetched = myclubs.partners().let { if (UrclubsConfiguration.Development.FAST_SYNC) it.take(5) else it }
        val partnersStored = partnerService.readAll(includeIgnored = true)

        val fetchedById = partnersFetched.associateBy { it.id }
        val fetchedIds = fetchedById.keys
        val storedById = partnersStored.associateBy { it.idMyc }
        val storedIds = storedById.keys

        val insertedPartners = fetchedById.minus(storedIds).values.map { partnerMyc ->
            val rawPartner = partnerMyc.toPartner()
            val detailPartner = myclubs.partner(rawPartner.shortName)
            val partner = rawPartner.enhance(detailPartner)
            partnerService.create(partner)
        }

        val deletedPartners = storedById.minus(fetchedIds).values
            .map { it.copy(deletedByMyc = true) }
            .apply {
                forEach {
                    log.trace { "Mark partner as deleted by myclubs: $it" }
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
        PostProcessor.process(it, detailed)
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
    note = "",
    shortName = shortName,
    rating = Rating.UNKNOWN,
    category = Category.UNKNOWN,
    maxCredits = Partner.DEFAULT_MAX_CREDITS,
    deletedByMyc = false,
    favourited = false,
    wishlisted = false,
    ignored = false,
    picture = PartnerImage.DefaultPicture,

    // will be enhanced later on by HTTP detail request
    linkMyclubs = "",
    linkPartner = "",
    addresses = emptyList(),
    tags = emptyList(),
    finishedActivities = emptyList()
)
