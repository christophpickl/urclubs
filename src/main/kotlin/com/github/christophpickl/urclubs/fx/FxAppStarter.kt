package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.MetaInf
import com.github.christophpickl.urclubs.QuitFXEvent
import com.github.christophpickl.urclubs.QuitManager
import com.github.christophpickl.urclubs.configureLogging
import com.github.christophpickl.urclubs.fx.partner.PartnersFxController
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerFxController
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerView
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersController
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.PropertiesFileCredentialsProvider
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.control.ButtonType
import javafx.stage.Screen
import javafx.stage.Stage
import tornadofx.*
import javax.swing.JOptionPane
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

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            log.error("Uncaught exception in thread '${thread.name}'!", throwable)
            JOptionPane.showMessageDialog(null, "App Crash!!! Aaaaaarg :-]", "UrClubs Crash", JOptionPane.ERROR_MESSAGE)
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        log.info { "Starting FX application context ..." }
        Application.launch(UrclubsFxApp::class.java, *args)
    }
}

object ApplicationStartedFxEvent : FXEvent()

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
    }

    override fun start(stage: Stage) {
        super.start(stage)
        val bounds = Screen.getPrimary().visualBounds
        val padding = 20.0

        stage.x = bounds.minX + padding
        stage.y = bounds.minY + padding
        stage.width = bounds.width - PartnerView.WIDTH - 5 * padding
        stage.height = bounds.height - 2 * padding

        fire(ApplicationStartedFxEvent)
    }

    override fun stop() { // <= Platform.exit()
        log.debug { "stop()" }
        guice.getInstance(QuitManager::class.java).publishQuitEvent()
        super.stop()
    }

    private fun registerEagerSingletons() {
        find(PartnersFxController::class)
        find(PartnerFxController::class)
        find(SyncFxController::class)
        find(FilterPartnersController::class)
        find(BrowseWebsiteController::class)
        find(MainController::class)
    }

}

class FxModule : AbstractModule() {
    override fun configure() {
        bind(Credentials::class.java).toProvider(PropertiesFileCredentialsProvider())
    }
}

class MainController : Controller() {

    private val logg = LOG {}
    private val metaInf: MetaInf by di()

    init {
        subscribe<QuitFXEvent> {
            logg.info { "QuitFXEvent was dispatched" }
            Platform.exit() // => UrclubsFxApp.stop()
        }
        subscribe<ShowAboutFXEvent> {
            information(
                title = "About",
                header = "About UrClubs",
                content = "Application Version: ${metaInf.version}\nhttps://github.com/christophpickl/urclubs",
                buttons = *arrayOf(ButtonType.OK)
            )
        }
    }

}
