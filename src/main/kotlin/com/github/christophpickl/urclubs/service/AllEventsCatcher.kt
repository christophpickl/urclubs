package com.github.christophpickl.urclubs.service

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

class AllEventsCatcher @Inject constructor(
        private val bus: EventBus
) {

    val log = LOG {}

    init {
        bus.register(this)
    }

    @Subscribe
    fun onAnyEvent(event: Any) {
        log.debug { "Got event: $event" }
    }

}
