package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.file.humanReadableSize
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerUpdatedEvent
import com.github.christophpickl.urclubs.domain.partner.Picture
import com.github.christophpickl.urclubs.fx.ApplicationStartedFxEvent
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import javafx.stage.Screen
import tornadofx.*
import java.io.File

class PartnerFxController : Controller() {

    private val logg = LOG {}
    private val view: PartnerView by inject()
    private val service: PartnerService by di()
    private val bus: EventBus by di()
    private val currentPartner: CurrentPartnerFx by inject()

    init {
        subscribe<ApplicationStartedFxEvent> { _ ->
            logg.debug { "Going to display partner detail view at very startup." }
            view.openWindow(
                resizable = true
            ).also {
                val bounds = Screen.getPrimary().visualBounds
                val width = PartnerView.WIDTH
                val padding = 20.0

                it!!.x = bounds.width - width - padding
                it.y = bounds.minY + padding
                it.width = width
            }
            primaryStage.requestFocus()
        }

        subscribe<PartnerSelectedEvent> { _ ->
            view.openWindow()
        }
        subscribe<PartnerSaveEvent> {
            service.update(currentPartner.toPartner())
        }
        subscribe<ChoosePictureFXEvent> {
            val file = choosePictureFile(it.requestor)
            if (file != null) {
                logg.debug { "Selected new picture: ${file.canonicalPath}" }
                val picture = Picture.FilePicture(file)

                if (picture.saveRepresentation.size > PartnerDbo.MAX_PICTURE_BYTES) {
                    alert(
                        type = Alert.AlertType.ERROR,
                        header = "Invalid Picture",
                        content = "Too big file! ${file.humanReadableSize} but max 1MB."
                    )
                } else {
                    currentPartner.pictureWrapper.set(picture)
                    currentPartner.picture.set(picture.fxImage)
                }
            }
        }
        bus.register(this)
    }

    private fun choosePictureFile(requestor: View): File? = FileChooser().apply {
        title = "Select Picture"
        initialDirectory = File(System.getProperty("user.home"))
        extensionFilters.add(FileChooser.ExtensionFilter("PNG", "*.png"))
    }.showOpenDialog(requestor.currentWindow)


    @Subscribe
    fun onPartnerUpdatedEvent(event: PartnerUpdatedEvent) {
        logg.debug { "onPartnerUpdatedEvent() redispatching guice event as javafx event" }
        fire(PartnerUpdatedFXEvent(event.partner))
    }

}
