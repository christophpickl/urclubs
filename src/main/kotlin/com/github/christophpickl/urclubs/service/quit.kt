package com.github.christophpickl.urclubs.service

import com.github.christophpickl.kpotpourri.common.logging.LOG

interface QuitListener {
    fun onQuit()
}

class QuitManager {

    private val log = LOG {}
    private val listeners = mutableListOf<QuitListener>()

    fun addQuitListener(listener: QuitListener) {
        log.debug { "addQuitListener(listener=$listener)" }
        listeners += listener
    }

    fun publishQuitEvent() {
        log.info { "publishQuitEvent() ... listeners.size=${listeners.size}" }
        listeners.forEach {
            it.onQuit()
        }
    }

}
