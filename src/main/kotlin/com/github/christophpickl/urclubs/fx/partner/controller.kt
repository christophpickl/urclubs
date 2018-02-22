package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import tornadofx.Controller

class PartnerFxController : Controller() {

    private val logg = LOG {}

    private val view: PartnerView by inject()

    init {
        subscribe<PartnerSelectedEvent> { event ->
            val partner = event.partner
            logg.debug { "Partner got selected: $partner" }
            view.nameField.text = partner.name

            view.openWindow()
        }
    }

}
