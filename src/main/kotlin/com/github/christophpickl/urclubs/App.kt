package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.logback4k.Logback4k


/*
https://www.myclubs.com/api/activities-get-partners
https://www.myclubs.com/api/categories-response
https://www.myclubs.com/api/cities-response
 */

object UrClubs {

    @JvmStatic
    fun main(args: Array<String>) {
        configureLogging()
        val email = args[0]
        val password = args[1]

        val myclubs = MyClubsApi(email, password)
        myclubs.login()
        myclubs.foo()
//        val user = myclubs.loggedUser()
//        println(user)
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
