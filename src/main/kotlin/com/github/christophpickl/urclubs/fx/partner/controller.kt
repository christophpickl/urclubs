package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerUpdatedFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.ApplyFilterFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import javafx.collections.FXCollections
import javafx.collections.transformation.FilteredList
import tornadofx.*

class PartnersFxController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()
    private val view: PartnersView by inject()
    private val allPartners = FXCollections.observableArrayList<Partner>()!!
    private val filteredPartners = FilteredList<Partner>(allPartners)

    init {
        view.table.items.bind(filteredPartners, { it })

        subscribe<PartnerListRequestFXEvent> {
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
            val index = allPartners.indexOfFirst { it.idDbo == updatedPartner.idDbo }
            if (index == -1) throw IllegalStateException("Could not find updated partner in table: $updatedPartner")
            allPartners[index] = updatedPartner
            view.table.selectionModel.select(index)
        }

        subscribe<ApplyFilterFXEvent> { event ->
            val filter = event.filter
            logg.debug { "Apply filter: $filter" }
            when (filter) {
                is Filter.NoFilter -> filteredPartners.predicate = filter.all
                is Filter.SomeFilter -> filteredPartners.predicate = filter.concatPredicates()
            }
        }
    }

}
