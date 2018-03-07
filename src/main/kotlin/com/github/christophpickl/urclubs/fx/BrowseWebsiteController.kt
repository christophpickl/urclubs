package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import tornadofx.*
import java.awt.Desktop
import java.net.URI

class OpenWebsiteFXEvent(val url: String) : FXEvent()

class BrowseWebsiteController: Controller() {

    private val logg = LOG {}

    init {
        subscribe<OpenWebsiteFXEvent> {
            openUrl(it.url)
        }
    }

    private fun openUrl(url: String) {
        logg.info { "openUrl(url=$url)" }
        Desktop.getDesktop().browse(URI(url))
    }

}
