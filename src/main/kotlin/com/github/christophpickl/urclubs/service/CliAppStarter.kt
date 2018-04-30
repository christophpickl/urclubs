package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncer
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
    private val upcomingActivitySyncer: UpcomingActivitySyncer,
    private val quitManager: QuitManager
) {
    fun start() {
        playground()

        quitManager.publishQuitEvent()
    }

    private fun playground() {
//        myclubs.courses(CourseFilter(
//            start = LocalDateTime.now().withHour(12),
//            end = LocalDateTime.now().withHour(23)
//        )).prettyPrint()

        upcomingActivitySyncer.sync()

//        partnerService.readAll().prettyPrint()
//        println(finishedActivitySyncer.sync())
//        println(partnerSyncer.sync())
    }

}

object CliAppStarter {
    init {
        UrClubsLogConfigurer.configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val guice = Guice.createInjector(
            MainModule(),
            CliModule()
        )
        val app = guice.getInstance(CliApp::class.java)
        app.start()
    }
}

class CliModule : AbstractModule() {
    override fun configure() {
        bind(CliApp::class.java)
    }
}
