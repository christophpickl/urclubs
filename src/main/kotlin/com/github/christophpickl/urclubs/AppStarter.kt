package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.google.inject.Guice

object AppStarter {

    init {
        configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val guice = Guice.createInjector(MainModule(args))
        val app = guice.getInstance(App::class.java)
        app.start()
    }

    private fun configureLogging() {
        Logback4k.reconfigure {
            rootLevel = Level.ALL
            packageLevel(Level.WARN, "org.apache.http")
            addConsoleAppender {
                pattern = "%highlight(%-5level) [%-32thread] %cyan(%logger{60}) - %msg%n"
            }
        }
    }

}
