package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.fx.partner.PartnersView
import com.github.christophpickl.urclubs.service.MetaInf
import de.codecentric.centerdevice.MenuToolkit
import javafx.scene.layout.Priority
import tornadofx.*
import java.util.Locale


class MainView : View() {

    private val partnersView: PartnersView by inject()
    private val menuBarController: MenuBarController by inject()
    private val metaInf: MetaInf by di()

    override val root = borderpane {
        addClass(Styles.mainPanel)
        style {
            if (UrclubsConfiguration.Development.COLOR_MODE) backgroundColor += javafx.scene.paint.Color.GREENYELLOW
        }

        val mybar = MyMenuBar(menuBarController).apply {
            isUseSystemMenuBar = true
        }
        if (UrclubsConfiguration.IS_MAC) {
            macMenuBar(mybar)
        } else {
            top {
                add(mybar)
            }
        }

        center {
            borderpane {
                vgrow = Priority.ALWAYS
                style {
                    if (UrclubsConfiguration.Development.COLOR_MODE) backgroundColor += javafx.scene.paint.Color.BEIGE
                }
                center {
                    add(partnersView)
                }
            }
        }
    }

    private fun macMenuBar(mybar: MyMenuBar) {
        MenuToolkit.toolkit(Locale.getDefault()).also { tk ->
            val aboutStage = de.codecentric.centerdevice.dialogs.about.AboutStageBuilder
                .start("About")
                .withAppName("UrClubs")
                .withCloseOnFocusLoss()
                .withHtml("""https://github.com/christophpickl/urclubs""")
                .withVersionString(metaInf.version)
                .build()

            tk.setApplicationMenu(tk.createDefaultApplicationMenu("UrClubs", aboutStage))
            tk.setGlobalMenuBar(mybar)
        }
    }

    init {
        title = "UrClubs" + if (UrclubsConfiguration.IS_DEVELOPMENT) " - DEVELOPMENT" else ""
        fire(PartnerListRequestFXEvent)
    }

}
