package com.github.christophpickl.urclubs.cli

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.QuitEvent
import com.github.christophpickl.urclubs.configureLogging
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.CourseEnhancer
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.SystemPropertyCredentialsProvider
import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javax.inject.Inject

class CliApp @Inject constructor(
    private val partnerSyncer: PartnerSyncer,
    private val finishedActivitySyncer: FinishedActivitySyncer,
    private val myclubs: MyClubsApi,
    private val partnerService: PartnerService,
    private val bus: EventBus,
    private val courseEnhancer: CourseEnhancer
) {
    fun start() {
        playground()

        bus.post(QuitEvent)
    }

    private fun playground() {
        partnerService.readAll().prettyPrint()

        // FIXME bug

        // FinishedActivityHtmlModel(date=2018-01-07T11:00, category=Fitnesskurs, title=Calisthenics Beginner,
        // locationHtml=Krafftgasse e.U.<br>Rembrandtstrasse 26, 1020 Wien)

        // - Partner{idDbo=85, shortName=krafftgasse-eu, name=Krafftgasse e.U., address=Rembrandtstrasse 26/2, 1020 Wien}

//        println(finishedActivitySyncer.sync())

//        println(partnerSyncer.sync())

    }

}

object CliAppStarter {
    init {
        configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val guice = Guice.createInjector(
            MainModule(),
            CliModule(args)
        )
        val app = guice.getInstance(CliApp::class.java)
        app.start()
    }
}

class CliModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
//        bind(Credentials::class.java).toProvider(CliArgsCredentialsProvider(args))
        bind(Credentials::class.java).toProvider(SystemPropertyCredentialsProvider())

        bind(CliApp::class.java)
    }

}
