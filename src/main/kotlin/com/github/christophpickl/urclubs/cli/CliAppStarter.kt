package com.github.christophpickl.urclubs.cli

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.QuitEvent
import com.github.christophpickl.urclubs.configureLogging
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.CliArgsCredentialsProvider
import com.github.christophpickl.urclubs.service.CourseEnhancer
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.FinishedActivityEnhancer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.github.christophpickl.urclubs.service.sync.PastActivitySyncer
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javax.inject.Inject

class CliApp @Inject constructor(
    private val partnerSyncer: PartnerSyncer,
    private val pastActivitySyncer: PastActivitySyncer,
    private val myclubs: MyClubsApi,
    private val partnerService: PartnerService,
    private val bus: EventBus,
    private val courseEnhancer: CourseEnhancer,
    private val activityEnhancer: FinishedActivityEnhancer
) {
    fun start() {
        playground()

        bus.post(QuitEvent)
    }

    private fun playground() {
//        partnerSyncer.sync()
        partnerService.readAll().prettyPrint()

//        pastActivitySyncer.sync()

//        myclubs.finishedActivities().prettyPrint()
//        println(myclubs.partner("hotpod-yoga-vienna"))

//        val finished = myclubs.finishedActivities()
//        activityEnhancer.enhance(finished.take(3)).prettyPrint()
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
        bind(Credentials::class.java).toProvider(CliArgsCredentialsProvider(args))
        bind(CliApp::class.java)
    }

}
