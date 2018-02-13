package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javax.inject.Inject

fun configureLogging() {
    Logback4k.reconfigure {
        rootLevel = Level.ALL
        packageLevel(Level.WARN, "org.apache.http")
        addConsoleAppender {
            pattern = "%highlight(%-5level) [%-32thread] %cyan(%logger{60}) - %msg%n"
        }
    }
}

class AllEventCatcher @Inject constructor(
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
