package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.google.common.annotations.VisibleForTesting

object PartnerSyncerPostProcessor {

    @VisibleForTesting
    fun process(partner: Partner, detailed: PartnerDetailHtmlModel): Partner {
        var processed = partner.copy(
            category = processCategory(detailed),
            ignored = detailed.tags.contains("Exklusiv für Frauen") // TODO i don't think that ALL courses are only for women... soo.... execute SQL in PROD DB and change
        )
        if (processed.category == Category.EMS) {
            processed = processed.copy(maxCredits = Partner.DEFAULT_MAX_CREDITS_EMS)
        }
        return processed
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
