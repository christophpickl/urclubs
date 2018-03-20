package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.service.sync.SyncReport
import com.github.christophpickl.urclubs.service.sync.SyncService
import javafx.scene.control.ButtonType
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.Window
import tornadofx.*

object SyncRequestFXEvent : FXEvent()

class SyncResultEvent(val syncReport: SyncReport) : FXEvent()

// see: http://blog.carl.pro/2016/05/modal-jaxafx-progress-indicator-running-in-background/
class ProgressDialog(
    private val owner: Window,
    private val displayedMessage: String
) {
    private val dialog = Stage(StageStyle.UNDECORATED)
    private val root = javafx.scene.Group()
    private val mainPane = javafx.scene.layout.BorderPane()
    private val width: Double = 330.0
    private val height: Double = 120.0
    private val scene = javafx.scene.Scene(root, width, height, javafx.scene.paint.Color.WHITE);

    init {
        dialog.initModality(Modality.WINDOW_MODAL)
        dialog.initOwner(owner)
        dialog.isResizable = false
    }

    fun show() {
        root.children.add(mainPane)
        mainPane.top = javafx.scene.control.Label(displayedMessage)
        dialog.scene = scene
        dialog.show()
        // Gets notified when task ended, but BEFORE result value is attributed. Using the observable list above is recommended.
//        dialog.setOnHiding {}
    }

    fun close() {
        dialog.close()
    }

}

class SyncFxController : Controller() {

    private val logg = LOG {}
    private val syncService: SyncService by di()

    init {
        subscribe<SyncRequestFXEvent> {
            logg.debug { "on SyncRequestFXEvent" }

            val progressDialog = ProgressDialog(primaryStage, "Sync in progress ...")
            progressDialog.show()

            runAsync {
                syncService.sync() // MINOR how are exceptions handled here?
            } ui { report ->
                progressDialog.close()
                fire(SyncResultEvent(report))
                fire(PartnerListRequestFXEvent)
            }
        }

        subscribe<SyncResultEvent> { event ->
            information(
                title = "Sync Report",
                header = "Sync completed successfully",
                content = "Partners inserted: ${event.syncReport.partners.insertedPartners.size}, " +
                    "deleted: ${event.syncReport.partners.deletedPartners.size}\n" +
                    "Past activities inserted: ${event.syncReport.finishedActivities.inserted.size}, ",
                buttons = *arrayOf(ButtonType.OK)
                // owner = ... main window reference?!
            )
        }
    }

}


