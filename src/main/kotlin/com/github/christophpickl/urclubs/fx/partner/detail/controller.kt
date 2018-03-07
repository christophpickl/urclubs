package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerUpdatedEvent
import com.github.christophpickl.urclubs.fx.ApplicationStartedFxEvent
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javafx.stage.Screen
import tornadofx.*

class PartnerFxController : Controller() {

    private val logg = LOG {}
    private val view: PartnerView by inject()
    private val service: PartnerService by di()
    private val bus: EventBus by di()
    private val currentPartner: CurrentPartnerFx by inject()

    init {
        subscribe<ApplicationStartedFxEvent> { _ ->
            logg.debug { "Going to display partner detail view at very startup." }
            view.openWindow(
                resizable = true
            ).also {
                val bounds = Screen.getPrimary().visualBounds
                val width = PartnerView.WIDTH
                val padding = 20.0

                it!!.x = bounds.width - width - padding
                it.y = bounds.minY + padding
                it.width = width
            }
            primaryStage.requestFocus()
        }

        subscribe<PartnerSelectedEvent> { _ ->
            view.openWindow()
        }
        subscribe<PartnerSaveEvent> {
            service.update(currentPartner.toPartner())
        }
        bus.register(this)
    }

    @Subscribe
    fun onPartnerUpdatedEvent(event: PartnerUpdatedEvent) {
        fire(PartnerUpdatedFXEvent(event.partner))
    }

}
