package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.logback4k.Logback4k

object UrClubs {

    init {
        configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val myclubs = MyClubsApi(parseCredentials(args))

        myclubs.login()
        myclubs.partners().prettyPrint()
    }

    private fun parseCredentials(args: Array<String>): Credentials {
        if (args.size != 2) {
            throw Exception("Expected exactly two arguments to be passed to the application!")
        }
        return Credentials(
                email = args[0],
                password = args[1]
        )
    }

    private fun configureLogging() {
        Logback4k.reconfigure {
            rootLevel = Level.ALL
            packageLevel(Level.WARN, "org.apache.http")
            addConsoleAppender {
                pattern = "[%-5level] %logger{60} - %msg%n"
            }
        }
    }

}

data class Credentials(
        val email: String,
        val password: String
)
