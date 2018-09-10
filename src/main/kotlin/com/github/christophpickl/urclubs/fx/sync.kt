package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.activity.FinishedActivity
import com.github.christophpickl.urclubs.domain.activity.UpcomingActivity
import com.github.christophpickl.urclubs.domain.partner.Partner
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
import javafx.scene.control.Dialog
import javafx.scene.control.TextArea
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.StageStyle.UNDECORATED
import javafx.stage.Window
import tornadofx.*
import java.time.LocalDateTime

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

class SyncReportDialog() : Dialog<ButtonType>() {

    private lateinit var textArea: TextArea

    init {
        val root = BorderPane()
        with(root) {
            style {
                padding = box(20.px)
            }
            center {
                vbox(10.0) {
                    alignment = Pos.CENTER
                    textArea = textarea {
                        isEditable = false
                    }
                    button("Close") {
                        action {
                            result = ButtonType.CLOSE
                            close()
                        }
                    }
                }
            }
        }
        initStyle(UNDECORATED) // MINOR UI: can't figure out how to support the window's close button ;)
        dialogPane.content = root
        title = "Sync Report"
    }

    fun updateView(event: SyncResultEvent) {
        val sb = StringBuilder()
        sb.append("Partners inserted (${event.syncReport.partners.insertedPartners.size}):\n")
        event.syncReport.partners.insertedPartners.forEach {
            sb.append("- ${it.name}\n")
        }
        sb.append("\n")
        sb.append("Partners deleted (${event.syncReport.partners.deletedPartners.size}):\n")
        event.syncReport.partners.deletedPartners.forEach {
            sb.append("- ${it.name}\n")
        }
        sb.append("\n")
        sb.append("Finished activities inserted (${event.syncReport.finishedActivities.inserted.size}):\n")
        event.syncReport.finishedActivities.inserted.forEach {
            sb.append("- ${it.title}\n")
        }
        textArea.text = sb.toString()
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
            SyncReportDialog().apply {
                updateView(event)
                show()
            }
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
                partners = PartnerSyncReport(
                        insertedPartners = listOf(Partner.prototype().copy(name = "Inserted Partner 1")),
                        deletedPartners = listOf(Partner.prototype().copy(name = "Deleted Partner 1"))
                ),
                finishedActivities = FinishedActivitySyncReport(
                        inserted = listOf(FinishedActivity.artificialInstance)
                ),
                upcomingActivities = UpcomingActivitySyncReport(
                        inserted = listOf(UpcomingActivity(1, "some title", LocalDateTime.now()))
                )
        )
    }

}

data class SyncReport(
        val partners: PartnerSyncReport,
        val finishedActivities: FinishedActivitySyncReport,
        val upcomingActivities: UpcomingActivitySyncReport
)
