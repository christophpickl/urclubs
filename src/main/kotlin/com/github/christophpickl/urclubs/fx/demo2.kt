package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import com.github.christophpickl.urclubs.fx.partner.PartnerListFXEvent
import com.github.christophpickl.urclubs.fx.partner.PartnersFxController
import com.google.inject.AbstractModule
import com.google.inject.Guice
import javafx.application.Application
import javafx.stage.Stage
import tornadofx.*
import kotlin.reflect.KClass

fun demo2LaunchJavaFx(viewClass: KClass<out View>) {
    Demo2App.viewClass = viewClass
    Application.launch(Demo2App::class.java)
}

class Demo2Module : AbstractModule() {
    override fun configure() {
        bind(PartnerService::class.java).toInstance(object : PartnerService {
            override fun create(partner: Partner) = partner
            override fun readAll(includeIgnored: Boolean) = emptyList<Partner>()
            override fun read(id: Long): Partner? = null
            override fun findByShortName(shortName: String): Partner? = null
            override fun findByShortNameOrThrow(shortName: String): Partner = throw UnsupportedOperationException("")
            override fun update(partner: Partner) {}
            override fun addArtificialFinishedActivity(partner: Partner) {}
            override fun searchPartner(locationHtml: String): Partner? = null
        })
    }
}

class Demo2App : App(
    primaryView = viewClass,
    stylesheet = Styles::class
) {
    companion object {
        var viewClass: KClass<out UIComponent>? = null
    }

    init {
        reloadStylesheetsOnFocus()

        val guice = Guice.createInjector(Demo2Module())
        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>) = guice.getInstance(type.java)
        }
        // eagerly load bean
        find(PartnersFxController::class)
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.width = 1100.0
        stage.height = 800.0
        stage.centerOnScreen()

        initApplicationState()
    }

    private fun initApplicationState() {
        find<CurrentPartnerFx>().initPartner(Partner.Dummies.superbEms)

        runAsync {
            fire(PartnerListFXEvent(Partner.Dummies.all))
        }
    }

}
