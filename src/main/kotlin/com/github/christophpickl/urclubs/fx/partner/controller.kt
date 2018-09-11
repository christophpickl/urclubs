package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerUpdatedFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.ApplyFilterFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import javafx.collections.FXCollections
import javafx.scene.control.TableView
import tornadofx.*

class PartnersFxController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()
    private val view: PartnersView by inject()
    private val allPartners = FXCollections.observableArrayList<Partner>()!!
    private val sortedFilteredPartners = SortedFilteredList<Partner>(allPartners)

    init {
        sortedFilteredPartners.bindTo(view.table)

        subscribe<PartnerListRequestFXEvent> {
            logg.debug { "on PartnerListRequestFXEvent" }
            val partners = partnerService.readAll()
            fire(PartnerListFXEvent(partners))
            runAsync {} ui {
                view.numberOfDisplayedPartners(partners.size)
            }
        }

        subscribe<IgnorePartnerFXEvent> { event ->
            logg.trace { "on IgnorePartnerFXEvent()" }
            val partner = event.partner
            partnerService.update(partner.copy(ignored = true))
        }

        subscribe<AddArtificialFinishedActivityFXEvent> { event ->
            logg.trace { "on AddArtificialFinishedActivityFXEvent()" }
            val partner = event.partner
            partnerService.addArtificialFinishedActivity(partner)
        }

        subscribe<PartnerListFXEvent> { event ->
            logg.trace { "on PartnerListFXEvent(event.partners.size=${event.partners.size}), updating table items." }
            allPartners.setAll(event.partners)
        }

        subscribe<PartnerUpdatedFXEvent> { event ->
            logg.trace { "on PartnerUpdatedFXEvent(event.partner=${event.partner})" }
            val updatedPartner = event.partner
            val indexInModel = allPartners.indexOfFirst { it.idDbo == updatedPartner.idDbo }
            if (indexInModel == -1) throw IllegalStateException("Could not find updated partner in table: $updatedPartner")
            allPartners[indexInModel] = updatedPartner
            view.table.selectPartnerIfVisible(updatedPartner)
        }

        subscribe<ApplyFilterFXEvent> { event ->
            logg.debug { "on ApplyFilterFXEvent(event.filter=${event.filter})" }
            val filter = event.filter
            when (filter) {
                is Filter.NoFilter -> sortedFilteredPartners.predicate = filter.all
                is Filter.SomeFilter -> sortedFilteredPartners.predicate = filter.concatPredicates()
            }
            runAsync {
                // moving the filter part in here makes the macAPP crash (stack overflow!) => com.sun.glass.ui.mac.MacAccessible.NSAccessibilityPostNotification
            } ui {
                view.numberOfDisplayedPartners(sortedFilteredPartners.size)
            }
        }
    }

    private fun TableView<Partner>.selectPartnerIfVisible(partnerToSelect: Partner) {
        val indexInTable = items.indexOfFirst { it.idDbo == partnerToSelect.idDbo }
        if (indexInTable != -1) {
            selectionModel.select(indexInTable)
        }
    }

}
