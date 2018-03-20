package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.UrClubsFxApp
import com.github.christophpickl.urclubs.service.configureLogging
import javafx.application.Application
import javax.swing.JOptionPane

object UrClubs {

    private val log = LOG {}

    init {
        configureLogging()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.error("Uncaught exception in thread '${thread.name}'!", throwable)
            JOptionPane.showMessageDialog(null, "App Crash!!! Aaaaaarg :-]", "UrClubs Crash", JOptionPane.ERROR_MESSAGE)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        log.info { "Starting FX application context ..." }
        Application.launch(UrClubsFxApp::class.java, *args)
    }

}
