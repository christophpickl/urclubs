package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.partners.PartnerListRequest
import com.github.christophpickl.urclubs.service.sync.PartnerSyncReport
import com.github.christophpickl.urclubs.service.sync.SyncService
import javafx.scene.control.ButtonType
import tornadofx.Controller
import tornadofx.EventBus
import tornadofx.FXEvent
import tornadofx.information

object SyncRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class SyncResultEvent(val partnersSynced: PartnerSyncReport) : FXEvent()

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
                    content = "Inserted: ${event.partnersSynced.insertedPartners.size}\n" +
                            "Deleted: ${event.partnersSynced.deletedPartners.size}",
                    buttons = *arrayOf(ButtonType.OK)
                    // owner = ... main window reference?!
            )
        }
    }

}


