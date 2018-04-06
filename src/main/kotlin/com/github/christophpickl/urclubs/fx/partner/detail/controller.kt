package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.file.humanReadableSize
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerImage
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.PartnerUpdatedEvent
import com.github.christophpickl.urclubs.fx.ImageFormat
import com.github.christophpickl.urclubs.fx.OpenWebsiteFXEvent
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.service.PrefsManager
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import javafx.collections.ListChangeListener
import javafx.scene.control.Alert
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File
import java.net.URLEncoder
import java.util.prefs.Preferences

class PartnerFxController : Controller() {

    private val logg = LOG {}
    private val service: PartnerService by di()
    private val prefsManager: PrefsManager by di()
    private val prefs: PartnerFxControllerPreferences
    private val detailView: PartnerDetailView by inject()
    private val bus: EventBus by di()
    private val currentPartner: CurrentPartnerFx by inject()

    init {
        prefs = PartnerFxControllerPreferences(prefsManager.newPrefs(javaClass))

        subscribe<OpenAddressFXEvent> {
            val encodedAddress = URLEncoder.encode(it.address, "UTF-8")
            fire(OpenWebsiteFXEvent(url = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"))
        }

        subscribe<RequestPartnerSaveFXEvent> {
            service.update(currentPartner.toPartner())
        }

        subscribe<ChoosePictureFXEvent> {
            val file = choosePictureFile(it.requestor)
            if (file != null) {
                prefs.choosenPath = file.parent
                logg.debug { "Selected new picture: ${file.canonicalPath}" }
                val format = ImageFormat.byName(file.extension.toLowerCase())
                        ?: throw Exception("Invalid file extension for: ${file.name} (supported: ${ImageFormat.values().joinToString()})")
                val picture = PartnerImage.FilePicture(file, format)

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

        currentPartner.addresses.addListener(ListChangeListener<String> {
            println("change: ${it.list}")
            detailView.addressesBox.clear()
            it.list.forEach { newAddress ->
                detailView.addressesBox.add(
                    javafx.scene.control.Hyperlink().apply {
                        textProperty().set(newAddress)
                        setOnAction { fire(OpenAddressFXEvent(address = newAddress)) }
                    }
                )
            }
        })
    }

    private fun choosePictureFile(requestor: View): File? = FileChooser().apply {
        title = "Select Picture"
        initialDirectory = File(prefs.choosenPath)
        extensionFilters.add(FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"))
    }.showOpenDialog(requestor.currentWindow)


    @Subscribe
    fun onPartnerUpdatedEvent(event: PartnerUpdatedEvent) {
        logg.debug { "onPartnerUpdatedEvent() redispatching guice event as javafx event" }
        fire(PartnerUpdatedFXEvent(event.partner))
    }

}


private class PartnerFxControllerPreferences(private val pref: Preferences) {

    var choosenPath: String
        get() = pref.get("choosenPath", System.getProperty("user.home"))
        set(value) = pref.put("choosenPath", value)

}
