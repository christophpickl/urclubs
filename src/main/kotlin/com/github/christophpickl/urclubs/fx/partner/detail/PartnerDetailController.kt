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

class PartnerDetailController : Controller() {

    private val logg = LOG {}

    private val bus: EventBus by di()
    private val partnerService: PartnerService by di()
    private val prefsManager: PrefsManager by di()

    private val detailView: PartnerDetailView by inject()
    private val currentPartner: CurrentPartnerFx by inject()

    private val prefs: PartnerDetailPreferences

    init {
        bus.register(this)
        prefs = PartnerDetailPreferences(prefsManager.newPrefs(javaClass))

        subscribe<OpenAddressFXEvent> {
            logg.debug { "on OpenAddressFXEvent()" }
            val encodedAddress = URLEncoder.encode(it.address, "UTF-8")
            fire(OpenWebsiteFXEvent(url = "https://www.google.com/maps/search/?api=1&query=$encodedAddress"))
        }

        subscribe<RemoveAddressFXEvent> { e ->
            logg.debug { "on RemoveAddressFXEvent()" }
            partnerService.update(currentPartner.toPartner().let { partner ->
                val removed = partner.addresses.toMutableList().apply { remove(e.address) }
                partner.copy(addresses = removed)
            })
        }

        subscribe<RequestPartnerSaveFXEvent> {
            logg.debug { "on RequestPartnerSaveFXEvent()" }
            partnerService.update(currentPartner.toPartner())
        }

        subscribe<ChoosePictureFXEvent> {
            logg.debug { "on ChoosePictureFXEvent()" }
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

        currentPartner.addresses.addListener(ListChangeListener<String> {
            logg.debug { "on address change()" }
            detailView.addressesBox.clear()
            it.list.forEach { newAddress ->
                detailView.addressesBox.add(
                    javafx.scene.control.Hyperlink().apply {
                        textProperty().set(newAddress)
                        setOnAction { fire(OpenAddressFXEvent(address = newAddress)) }
                        contextmenu {
                            item(name = "Open in Browser") {
                                setOnAction { fire(OpenAddressFXEvent(address = newAddress)) }
                            }
                            item(name = "Remove Address") {
                                setOnAction { fire(RemoveAddressFXEvent(address = newAddress)) }
                            }
                        }
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
        currentPartner.addresses.set(event.partner.addresses.observable())
    }

}


private class PartnerDetailPreferences(private val pref: Preferences) {

    var choosenPath: String
        get() = pref.get("choosenPath", System.getProperty("user.home"))
        set(value) = pref.put("choosenPath", value)

}
