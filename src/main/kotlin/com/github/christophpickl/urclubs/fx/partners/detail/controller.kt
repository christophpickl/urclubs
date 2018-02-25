package com.github.christophpickl.urclubs.fx.partners.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerUpdatedEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import tornadofx.*

class PartnerFxController : Controller() {

    private val logg = LOG {}
    private val view: PartnerView by inject()
    private val service: PartnerService by di()
    private val bus: EventBus by di()
    private var currentPartner: Partner? = null

    init {
        subscribe<PartnerSelectedEvent> { event ->
            val partner = event.partner
            logg.debug { "Partner got selected: $partner" }
            currentPartner = partner
            view.initPartner(partner)
            view.openWindow()
        }
        subscribe<PartnerSaveEvent> {
            val updatedPartner = view.readPartner(currentPartner!!)
            service.update(updatedPartner)
        }
        bus.register(this)
    }

    @Subscribe
    fun onPartnerUpdatedEvent(event: PartnerUpdatedEvent) {
        fire(PartnerUpdatedFXEvent(event.partner))
    }

    private fun PartnerView.initPartner(partner: Partner) {
        nameField.text = partner.name
        category.selectionModel.select(partner.category)
    }

    private fun PartnerView.readPartner(partner: Partner) = partner.copy(
        name = nameField.text.trim(),
        category = category.selectedItem!!
    )

}
