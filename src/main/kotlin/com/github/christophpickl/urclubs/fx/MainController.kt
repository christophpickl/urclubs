package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.service.MetaInf
import javafx.application.Platform
import javafx.scene.control.ButtonType
import tornadofx.*

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
