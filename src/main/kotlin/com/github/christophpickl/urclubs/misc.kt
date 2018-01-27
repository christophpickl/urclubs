package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.logback4k.Logback4k

fun configureLogging() {
    Logback4k.reconfigure {
        rootLevel = Level.ALL
        packageLevel(Level.WARN, "org.apache.http")
        addConsoleAppender {
            pattern = "%highlight(%-5level) [%-32thread] %cyan(%logger{60}) - %msg%n"
        }
    }
}
