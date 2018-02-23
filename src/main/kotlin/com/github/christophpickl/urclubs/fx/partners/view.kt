package com.github.christophpickl.urclubs.fx.partners

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.PartnerSelectedEvent
import com.github.christophpickl.urclubs.fx.partner.PartnerUpdatedFXEvent
import tornadofx.SmartResize
import tornadofx.View
import tornadofx.borderpane
import tornadofx.center
import tornadofx.column
import tornadofx.onUserSelect
import tornadofx.tableview

class PartnersView : View() {

    private val logg = LOG {}

    private val table = tableview<Partner> {
        column("Name", Partner::name)
        column("Rating", Partner::rating)
        column("Address", Partner::address)
        column("WWW partner", Partner::linkPartnerSite)
        column("WWW myclubs", Partner::linkMyclubsSite)
        columnResizePolicy = SmartResize.POLICY
    }

    override val root = borderpane {
        center {
            add(table)
        }
    }

    init {
        subscribe<PartnerListEvent> { event ->
            logg.trace { "Received PartnerListEvent (partners.size=${event.partners.size}), updating table items." }
            table.items.setAll(event.partners)
        }
        subscribe<PartnerUpdatedFXEvent> { event ->
            val partner = event.partner
            logg.trace { "Updating partner in table: $partner" }
            val index = table.items.indexOfFirst { it.idDbo == partner.idDbo }
            if (index == -1) throw IllegalStateException("Could not find updated partner in table: $partner") // TODO when search (=filter) is active this will fail??
            table.items[index] = partner
        }
        table.onUserSelect { partner ->
            logg.trace { "User selected partner in table: $partner" }
            fire(PartnerSelectedEvent(partner))
        }
    }

}
