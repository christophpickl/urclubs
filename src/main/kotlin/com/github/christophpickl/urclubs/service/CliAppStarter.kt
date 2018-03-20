package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
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
    private val courseEnhancer: CourseEnhancer,
    private val quitManager: QuitManager
) {
    fun start() {
        playground()

        quitManager.publishQuitEvent()
    }

    private fun playground() {
        // FinishedActivityHtmlModel(date=2017-10-16T16:19, category=EMS, title=EMS-Training,
        // locationHtml=Bodystreet Convalere<br>Ungargasse 46, 1030 Wien)

//        partnerService.readAll().prettyPrint()
        println(finishedActivitySyncer.sync())
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
        bind(Credentials::class.java).toProvider(PropertiesFileCredentialsProvider())

        bind(CliApp::class.java)
    }

}
