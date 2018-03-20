package com.github.christophpickl.urclubs.service

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.urclubs.UrclubsConfiguration

object UrClubsLogConfigurer {

    fun configureLogging() {
        if (UrclubsConfiguration.IS_LOGS_DISABLED) {
            return
        }

        if (UrclubsConfiguration.IS_DEVELOPMENT) {
            configureConsoleDevOnly()
        } else {
            configureProductionLog()
        }
        logWelcomeMessage()
    }

    private fun configureProductionLog() {
        Logback4k.reconfigure {
            rootLevel = Level.WARN
            packageLevel(Level.DEBUG, "com.github.christophpickl.urclubs")

            addFileAppender {
                val filePrefix = "${UrclubsConfiguration.URCLUBS_LOGS_DIRECTORY}/urclubs"
                println("UrClubs logs can be found here: ${UrclubsConfiguration.URCLUBS_LOGS_DIRECTORY}")

                file = "$filePrefix.log"
                filePattern = "$filePrefix-%d{yyyy-MM-dd}.log"
                pattern = "%d{yyyy-MM-dd HH:mm:ss.SSS} [%-5level] [%-28thread] %logger{60} - %msg%n"
            }
        }
    }

    private fun configureConsoleDevOnly() {
        Logback4k.reconfigure {
            rootLevel = Level.WARN
            packageLevel(Level.ALL, "com.github.christophpickl.urclubs")

            addConsoleAppender {
                pattern = "%d{HH:mm:ss.SSS} %highlight(%-5level) [%-32thread] %cyan(%logger{60}) - %msg%n"
            }
        }
    }

    private fun logWelcomeMessage() {
        LOG {}.info {
            """
  _    _       _____ _       _
 | |  | |     / ____| |     | |
 | |  | |_ __| |    | |_   _| |__  ___
 | |  | | '__| |    | | | | | '_ \/ __|
 | |__| | |  | |____| | |_| | |_) \__ \
  \____/|_|   \_____|_|\__,_|_.__/|___/

"""
        }
    }
}
