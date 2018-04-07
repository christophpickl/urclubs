package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.google.common.annotations.VisibleForTesting

object PartnerSyncerPostProcessor {

    private val knownTagsToCategory = linkedMapOf(
        "fitnessstudio" to Category.GYM,
        "kampfkunst" to Category.WUSHU,
        "yoga" to Category.YOGA,
        "fitnesskurse" to Category.WORKOUT,
        "ems" to Category.EMS,
        "boxen" to Category.WUSHU,
        "wassersport" to Category.WATER,
        "dance" to Category.DANCE,
        "pilates" to Category.PILATES,
        "crosstraining" to Category.WORKOUT
    )

    @VisibleForTesting
    fun process(partner: Partner, detailed: PartnerDetailHtmlModel): Partner {
        var processed = partner.copy(
            category = processCategory(detailed)
        )
        if (processed.category == Category.EMS) {
            processed = processed.copy(maxCredits = Partner.DEFAULT_MAX_CREDITS_EMS)
        }
        return processed
    }

    private fun processCategory(detailed: PartnerDetailHtmlModel): Category {
        val loweredTags = detailed.tags.map { it.toLowerCase() }
        return knownTagsToCategory.entries.firstOrNull {
            loweredTags.anyContains(it.key)
        }?.value ?: Category.UNKNOWN
    }

    private fun List<String>.anyContains(search: String) = any { it.contains(search) }
}
