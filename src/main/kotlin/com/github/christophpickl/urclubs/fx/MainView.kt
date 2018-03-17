package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.DEVELOPMENT_COLORS
import com.github.christophpickl.urclubs.IS_DEVELOPMENT
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.PartnerListEvent
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequest
import com.github.christophpickl.urclubs.fx.partner.PartnersView
import javafx.application.Application
import javafx.scene.layout.Priority
import javafx.stage.Stage
import tornadofx.*


fun main(args: Array<String>) {
    class DummyApp : App(primaryView = MainView::class) {
        override fun start(stage: Stage) {
            super.start(stage)
            stage.width = 1100.0
            stage.height = 800.0
            stage.centerOnScreen()
            fire(PartnerListEvent(Partner.Dummies.all))
        }
    }
    Application.launch(DummyApp::class.java, *args)
}

class MainView : View() {

    private val bottomView: BottomView by inject()
    private val partnersView: PartnersView by inject()
    private val menuBarController: MenuBarController by inject()

    override val root = vbox {
        style {
            if (DEVELOPMENT_COLORS) backgroundColor += javafx.scene.paint.Color.GREENYELLOW
        }

        add(MyMenuBar(menuBarController))

        borderpane {
            vgrow = Priority.ALWAYS
            style {
                if (DEVELOPMENT_COLORS) backgroundColor += javafx.scene.paint.Color.BEIGE
            }
            center {
                add(partnersView)
            }
            bottom {
                add(bottomView)
            }
        }
    }

    init {
        title = "UrClubs" + if (IS_DEVELOPMENT) " - DEVELOPMENT" else ""
        fire(PartnerListRequest)
    }
}

class BottomView : View() {

    override val root = hbox {
        button("Resync Data").action {
            fire(SyncRequest)
        }
        button("Reload from DB").action {
            fire(PartnerListRequest)
        }
    }

}
