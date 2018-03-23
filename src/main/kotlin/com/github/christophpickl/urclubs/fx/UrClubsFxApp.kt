package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.MainModule
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.fx.partner.PartnersFxController
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerFxController
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerView
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersController
import com.github.christophpickl.urclubs.service.QuitManager
import com.google.inject.Guice
import javafx.stage.Screen
import javafx.stage.Stage
import tornadofx.*
import kotlin.reflect.KClass

// official page ... https://github.com/edvin/tornadofx
// https://www.scribd.com/document/367520392/Tornadofx-Guide
// https://edvin.gitbooks.io/tornadofx-guide/content/
// https://sites.google.com/a/athaydes.com/renato-athaydes/posts/saynotoelectronusingjavafxtowriteafastresponsivedesktopapplication
// guice+fx => https://gluonhq.com/labs/ignite/

class UrClubsFxApp : App(
    primaryView = MainView::class,
    stylesheet = Styles::class
) {

    companion object {
        val WINDOW_GAP = 10.0
    }
    private val log = LOG {}

    private val guice = Guice.createInjector(MainModule())

    init {
        log.info { "FX application started." }
        if (UrclubsConfiguration.IS_DEVELOPMENT) {
            reloadStylesheetsOnFocus()
        }

        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
        registerEagerSingletons()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        val bounds = Screen.getPrimary().visualBounds
        val padding = WINDOW_GAP

        stage.x = bounds.minX
        stage.y = bounds.minY
        stage.width = bounds.width - PartnerView.WINDOW_WIDTH - padding
        stage.height = bounds.height

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
