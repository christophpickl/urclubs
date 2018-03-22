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
//    private val filteredPartners = FilteredList<Partner>(allPartners)
    private val sortedFilteredPartners = SortedFilteredList<Partner>(allPartners)

    init {
        sortedFilteredPartners.bindTo(view.table)

        subscribe<PartnerListRequestFXEvent> {
            logg.debug { "on PartnerListRequestFXEvent" }
            val partners = partnerService.readAll()
            fire(PartnerListFXEvent(partners))
        }

        subscribe<IgnorePartnerFXEvent> {
            val partner = it.partner
            partnerService.update(partner.copy(ignored = true))
        }

        subscribe<PartnerListFXEvent> { event ->
            logg.trace { "Received PartnerListEvent (partners.size=${event.partners.size}), updating table items." }
            allPartners.setAll(event.partners)
        }

        subscribe<PartnerUpdatedFXEvent> { event ->
            val updatedPartner = event.partner
            logg.trace { "Updating partner in table: $updatedPartner" }
            val indexInModel = allPartners.indexOfFirst { it.idDbo == updatedPartner.idDbo }
            if (indexInModel == -1) throw IllegalStateException("Could not find updated partner in table: $updatedPartner")
            allPartners[indexInModel] = updatedPartner
            view.table.selectPartnerIfVisible(updatedPartner)
        }

        subscribe<ApplyFilterFXEvent> { event ->
            val filter = event.filter
            logg.debug { "Apply filter: $filter" }
            when (filter) {
                is Filter.NoFilter -> sortedFilteredPartners.predicate = filter.all
                is Filter.SomeFilter -> sortedFilteredPartners.predicate = filter.concatPredicates()
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
