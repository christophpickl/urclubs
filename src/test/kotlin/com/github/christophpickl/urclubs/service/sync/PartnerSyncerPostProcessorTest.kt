package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.myclubs.parser.PartnerDetailHtmlModel
import com.github.christophpickl.urclubs.myclubs.testInstance
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test

@Test
class PartnerSyncerPostProcessorTest {

    private val notYoga = Category.DANCE
    private val partner = Partner.testInstance
    private val detail = PartnerDetailHtmlModel.testInstance()

    fun `Given tags containing yoga Then cateogry should be overwritten to yoga`() {
        val processed = PartnerSyncerPostProcessor.process(
            partner.copy(category = notYoga),
            detail.copy(tags = listOf("yoga")))

        assertThat(processed.category).isEqualTo(Category.YOGA)
    }

    fun `Given EMS tags Then set category to EMS and max credits to 2`() {
        val processed = PartnerSyncerPostProcessor.process(
            partner,
            detail.copy(tags = listOf("ems")))

        assertThat(processed.category).isEqualTo(Category.EMS)
        assertThat(processed.maxCredits).isEqualTo(2)
    }

}
