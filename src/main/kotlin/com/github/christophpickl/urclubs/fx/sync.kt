package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequest
import com.github.christophpickl.urclubs.service.sync.SyncReport
import com.github.christophpickl.urclubs.service.sync.SyncService
import javafx.scene.control.ButtonType
import tornadofx.*

object SyncRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class SyncResultEvent(val syncReport: SyncReport) : FXEvent()

class SyncFxController : Controller() {

    private val logg = LOG {}
    private val syncService: SyncService by di()

    init {
        subscribe<SyncRequest> {
            // TODO block UI, show spinning wheel, disable buttons... maybe set global bindable boolean flag "isSyncInProgress"
            logg.debug { "Got SyncRequest..." }
            val report = syncService.sync()
            fire(SyncResultEvent(report))
            fire(PartnerListRequest)
        }
        subscribe<SyncResultEvent> { event ->
            information(
                    title = "Sync Report",
                    header = "Sync completed successfully",
                content = "Partners inserted: ${event.syncReport.partners.insertedPartners.size}, " +
                    "deleted: ${event.syncReport.partners.deletedPartners.size}\n" +
                    "Past activities inserted: ${event.syncReport.finishedActivities.inserted.size}, " +
                    "deleted: ${event.syncReport.finishedActivities.deleted.size}\n",
                    buttons = *arrayOf(ButtonType.OK)
                    // owner = ... main window reference?!
            )
        }
    }

}


