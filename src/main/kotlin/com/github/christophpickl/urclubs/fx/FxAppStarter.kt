package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.QuitEvent
import com.github.christophpickl.urclubs.configureLogging
import com.github.christophpickl.urclubs.fx.partner.PartnerFxController
import com.github.christophpickl.urclubs.fx.partners.PartnersFxController
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.SystemPropertyCredentialsProvider
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javafx.application.Application
import tornadofx.App
import tornadofx.DIContainer
import tornadofx.FX
import tornadofx.find
import kotlin.reflect.KClass

// official page ... https://github.com/edvin/tornadofx
// https://www.scribd.com/document/367520392/Tornadofx-Guide
// https://edvin.gitbooks.io/tornadofx-guide/content/
// https://sites.google.com/a/athaydes.com/renato-athaydes/posts/saynotoelectronusingjavafxtowriteafastresponsivedesktopapplication
// guice+fx => https://gluonhq.com/labs/ignite/

object FxAppStarter {

    private val log = LOG {}

    init {
        configureLogging()
    }

    @JvmStatic
    fun main(args: Array<String>) {
        log.info { "Starting FX application context ..." }
        Application.launch(UrclubsFxApp::class.java, *args)
    }
}

class UrclubsFxApp : App(
        primaryView = MainView::class,
        stylesheet = Styles::class
) {

    private val log = LOG {}

    private val guice = Guice.createInjector(
            MainModule(), FxModule()
    )

    init {
        log.info { "FX application started." }
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
        registerEagerSingletons()
        val myclubs = guice.getInstance(MyClubsApi::class.java)
        myclubs.login() // TODO do a lazy login when requested first, instead eager here
        log.info { "Logged in user: ${myclubs.loggedUser()}" }
    }

//    override fun start(stage: Stage) {
//        super.start(stage)
//    }

    override fun stop() { // <= Platform.exit()
        log.debug { "stop()" }
        guice.getInstance(EventBus::class.java).post(QuitEvent) // MINOR block until DB was closed
        super.stop()
    }

    private fun registerEagerSingletons() {
        find(PartnersFxController::class)
        find(PartnerFxController::class)
        find(SyncFxController::class)
    }

}

class FxModule : AbstractModule() {
    override fun configure() {
        bind(Credentials::class.java).toProvider(SystemPropertyCredentialsProvider())
    }
}
