package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerSelectedEvent
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerUpdatedFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView
import tornadofx.*

class PartnersView : View() {

    private val logg = LOG {}

    // https://github.com/edvin/tornadofx/wiki/Utilities
    private val partnersFilter: FilterPartnersView by inject()
    private val currentPartner: CurrentPartnerFx by inject()

    val table = tableview<Partner> {
        column("Name", Partner::name)
        column("Category", Partner::category)
        column("Rating", Partner::rating)
        column("Address", Partner::address)
        columnResizePolicy = SmartResize.POLICY
    }

    override val root = borderpane {
        top {
            add(partnersFilter)
        }
        center {
            add(table)
        }
    }

    init {
        subscribe<PartnerListEvent> { event ->
            logg.trace { "Received PartnerListEvent (partners.size=${event.partners.size}), updating table items." }
            // TODO go through controller instead and apply filter
            table.items.setAll(event.partners)
        }
        subscribe<PartnerUpdatedFXEvent> { event ->
            val partner = event.partner
            logg.trace { "Updating partner in table: $partner" }
            val index = table.items.indexOfFirst { it.idDbo == partner.idDbo }
            if (index == -1) throw IllegalStateException("Could not find updated partner in table: $partner") // TODO when search (=filter) is active this will fail??
            table.items[index] = partner // FIXME when filtering, saving doesnt work... need to set on original list somehow...
        }
        table.onUserSelect { partner ->
            logg.trace { "User selected partner in table: $partner" }
//            currentPartner.partner.set(partner.toPartnerFx())
            currentPartner.initPartner(partner)
            fire(PartnerSelectedEvent(partner))
        }
    }

}
