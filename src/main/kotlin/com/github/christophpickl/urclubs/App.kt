package com.github.christophpickl.urclubs

import ch.qos.logback.classic.Level
import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.kpotpourri.logback4k.Logback4k
import com.github.christophpickl.urclubs.backend.MyClubsApi
import com.github.christophpickl.urclubs.backend.MyClubsHttpApi
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.service.PartnerService
import com.github.christophpickl.urclubs.service.PartnerServiceImpl
import com.github.christophpickl.urclubs.service.Syncer
import com.github.christophpickl.urclubs.service.SyncerReport
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
        private val syncer: Syncer,
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService,
        private val bus: EventBus
) {
    fun start() {
        // Partner(idDbo=118, idMyc=yzWykzkxDX, name=Schwimmschule Wien, rating=UNKNOWN)

//       myclubs.login()
//       println(myclubs.loggedUser())
//       val partnersMyc = myclubs.partners()

//       partnerService.create(Partner(idDbo = 0L, name = "foobar"))

//        println(syncer.sync().toPrettyString())

//        val schwimmschule = partnerService.read(id = 118)!!
//        partnerService.update(schwimmschule.copy(rating = Rating.SUPERB))

        partnerService.readAll().prettyPrint()

//       myclubs.activities().prettyPrint()
        bus.post(QuitEvent)
    }
}

private fun SyncerReport.toPrettyString() =
        """Sync Report:
==========================
Inserted (${insertedPartners.size}):
--------------------------
${insertedPartners.toPrettyString()}

Deleted(${deletedPartners.size}):
--------------------------
${deletedPartners.toPrettyString()}
"""

class MainModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
        bind(EventBus::class.java).toInstance(EventBus())
        bind(Credentials::class.java).toProvider(CredentialsProvider(args))

        install(PersistenceModule())

        bind(PartnerService::class.java).to(PartnerServiceImpl::class.java)
        bind(MyClubsApi::class.java).to(MyClubsHttpApi::class.java)
        bind(UrClubsApp::class.java)
        bind(Syncer::class.java)
    }
}

class CredentialsProvider(private val args: Array<String>) : Provider<Credentials> {
    override fun get(): Credentials {
        if (args.size != 2) {
            throw Exception("Please provide credentials via CLI arguments!")
        }
        return Credentials(
                email = args[0],
                password = args[1]
        )
    }
}

object QuitEvent
