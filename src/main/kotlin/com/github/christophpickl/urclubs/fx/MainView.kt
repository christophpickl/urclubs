package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.fx.partner.PartnersView
import javafx.scene.layout.Priority
import tornadofx.*


class MainView : View() {

    private val bottomView: BottomView by inject()
    private val partnersView: PartnersView by inject()
    private val menuBarController: MenuBarController by inject()

    override val root = vbox {
        addClass(Styles.mainPanel)
        style {
            if (UrclubsConfiguration.Development.COLOR_MODE) backgroundColor += javafx.scene.paint.Color.GREENYELLOW
        }

        add(MyMenuBar(menuBarController))

        borderpane {
            vgrow = Priority.ALWAYS
            style {
                if (UrclubsConfiguration.Development.COLOR_MODE) backgroundColor += javafx.scene.paint.Color.BEIGE
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
        title = "UrClubs" + if (UrclubsConfiguration.IS_DEVELOPMENT) " - DEVELOPMENT" else ""
        fire(PartnerListRequestFXEvent)
    }
}

class BottomView : View() {

    override val root = hbox {
        paddingTop = Styles.partnersTableVerticalPadding

        button("Resync Data").action {
            fire(SyncRequestFXEvent)
        }
    }

}
