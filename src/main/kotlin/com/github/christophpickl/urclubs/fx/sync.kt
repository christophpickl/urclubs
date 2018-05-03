package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncReport
import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncReport
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncReport
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncer
import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Scene
import javafx.scene.control.ButtonType
import javafx.scene.paint.Color
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
    private val width: Double = 330.0
    private val height: Double = 120.0

    init {
        dialog.initModality(Modality.WINDOW_MODAL)
        dialog.initOwner(owner)
        dialog.isResizable = false
    }

    fun show() {
        val root = javafx.scene.layout.BorderPane().apply {
            style {
                padding = box(10.px)
                hAlignment = HPos.CENTER
                vAlignment = VPos.CENTER
                if (UrclubsConfiguration.Development.COLOR_MODE) {
                    backgroundColor += Styles.green
                }
            }
            center {
                vbox {
                    alignment = Pos.CENTER
                    spacing = 5.0

                    label(text = displayedMessage)
                    progressindicator {
                        progress = -1.0 // make it indeterministic
                    }
                }
            }
        }
        dialog.scene = Scene(root, width, height, Color.WHITE);
        dialog.show()
    }

    fun close() {
        dialog.close()
    }

}

class SyncFxController : Controller() {

    private val logg = LOG {}
    private val partnerSyncer: PartnerSyncer by di()
    private val finishedActivitySyncer: FinishedActivitySyncer by di()
    private val upcomingActivitySyncer: UpcomingActivitySyncer by di()

    init {
        subscribe<SyncRequestFXEvent> {
            logg.debug { "on SyncRequestFXEvent" }

            val progressDialog = ProgressDialog(primaryStage, "Sync in progress ...")
            progressDialog.show()

            runAsyncSafely(
                    onAny = { progressDialog.close() },
                    onSuccess = { report -> fire(SyncResultEvent(report)) },
                    dialogContent = FailDialogContent(title = "Resync Data", header = "Resyncing data from MyClubs failed!")
            ) {
                executeSync()
            }
        }

        subscribe<SyncResultEvent> { event ->
            information(
                    title = "Sync Report",
                    header = "Sync completed successfully",
                    content =
                    "Partners inserted: ${event.syncReport.partners.insertedPartners.size}\n" +
                            "Partners deleted: ${event.syncReport.partners.deletedPartners.size}\n" +
                            "Finished activities inserted: ${event.syncReport.finishedActivities.inserted.size}",
                    buttons = *arrayOf(ButtonType.OK)
                    // owner = ... main window reference?!
            )
        }
    }

    private fun executeSync(): SyncReport {
        if (UrclubsConfiguration.Development.STUBBED_SYNCER) {
            return stubbedSync()
        }

        val partnersReport = partnerSyncer.sync()
        fire(PartnerListRequestFXEvent)

        val finishedActivitiesReport = finishedActivitySyncer.sync()
        val upcomingActivitiesReport = upcomingActivitySyncer.sync()

        return SyncReport(
                partners = partnersReport,
                finishedActivities = finishedActivitiesReport,
                upcomingActivities = upcomingActivitiesReport
        )
    }

    private fun stubbedSync(): SyncReport {
        logg.debug { "stubbedSync()" }
        Thread.sleep(3 * 1000)
        return SyncReport(
                partners = PartnerSyncReport(emptyList(), emptyList()),
                finishedActivities = FinishedActivitySyncReport(emptyList()),
                upcomingActivities = UpcomingActivitySyncReport(emptyList())
        )
    }

}

data class SyncReport(
        val partners: PartnerSyncReport,
        val finishedActivities: FinishedActivitySyncReport,
        val upcomingActivities: UpcomingActivitySyncReport
)
