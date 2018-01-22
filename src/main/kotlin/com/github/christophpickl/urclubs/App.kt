package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.urclubs.backend.MyClubsApi
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.service.PartnerService
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Provider
import javax.inject.Inject

object AppStarter {

    init {
        configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val guice = Guice.createInjector(MainModule(args))
        val app = guice.getInstance(UrClubsApp::class.java)
        app.start()
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

class UrClubsApp @Inject constructor(
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService,
        private val bus: EventBus
) {
   fun start() {

//       myclubs.login()
//       println(myclubs.loggedUser())
//       val partnersMyc = myclubs.partners()

//       partnerService.insert(Partner(idDbo = 0L, name = "foobar"))
       val partners = partnerService.fetchAll()
       println(partners)

//       myclubs.activities().prettyPrint()
       bus.post(QuitEvent)
    }
}

class MainModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
        bind(Credentials::class.java).toProvider(CredentialsProvider(args))
        bind(EventBus::class.java).toInstance(EventBus())
        install(PersistenceModule())
        bind(MyClubsApi::class.java)
        bind(UrClubsApp::class.java)
    }
}

class CredentialsProvider(private val args: Array<String>) : Provider<Credentials> {
    override fun get(): Credentials {
        if (args.size != 2) {
            throw Exception("Expected exactly two arguments to be passed to the application!")
        }
        return Credentials(
                email = args[0],
                password = args[1]
        )
    }
}

object QuitEvent
