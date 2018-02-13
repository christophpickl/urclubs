package com.github.christophpickl.urclubs.fx.partners

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.SmartResize
import tornadofx.View
import tornadofx.borderpane
import tornadofx.center
import tornadofx.column
import tornadofx.tableview

class PartnersView : View() {

    private val logg = LOG {}

    override val root = borderpane {
        center {
            tableview<Partner> {
                column("Name", Partner::name)
                column("Rating", Partner::rating)
                column("Address", Partner::address)
                columnResizePolicy = SmartResize.POLICY
                subscribe<PartnerListEvent> { event ->
                    logg.trace { "Received PartnerListEvent (partners.size=${event.partners.size}), updating table items." }
                    items.setAll(event.partners)
                }
            }
        }
    }

}
