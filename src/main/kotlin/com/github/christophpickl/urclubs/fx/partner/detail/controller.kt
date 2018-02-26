package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerUpdatedEvent
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import tornadofx.*

class PartnerFxController : Controller() {

    private val logg = LOG {}
    private val view: PartnerView by inject()
    private val service: PartnerService by di()
    private val bus: EventBus by di()
    private val currentPartner: CurrentPartnerFx by inject()

    init {
        subscribe<PartnerSelectedEvent> { event ->
//            val partner = event.partner
//            logg.debug { "Partner got selected: $partner" }
//            currentPartner.partner.set(partner.toPartnerFx())
//            view.initPartner(partner)
            view.openWindow()
        }
//        currentPartner.partner.addListener { it ->
//            view.openWindow()
//        }
        subscribe<PartnerSaveEvent> {
//            val updatedPartner = view.readPartner(currentPartnerOld!!)
            service.update(currentPartner.toPartner())
        }
        bus.register(this)
    }

    @Subscribe
    fun onPartnerUpdatedEvent(event: PartnerUpdatedEvent) {
        fire(PartnerUpdatedFXEvent(event.partner))
    }

//    private fun PartnerView.initPartner(partner: Partner) {
//        nameField.text = partner.name
//        category.selectionModel.select(partner.category)
//    }

//    private fun PartnerView.readPartner(partner: Partner) = partner.copy(
//        name = nameField.text.trim(),
//        name = currentPartner.partner.get().name.get(),
//        category = category.selectedItem!!
//    )

}
