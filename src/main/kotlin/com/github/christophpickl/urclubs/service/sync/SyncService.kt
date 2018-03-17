package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.inject.Inject

class SyncService @Inject constructor(
    private val partnerSyncer: PartnerSyncer,
    private val finishedActivitySyncer: FinishedActivitySyncer,
    private val upcomingActivitySyncer: UpcomingActivitySyncer
) {

    private val log = LOG {}

    fun sync(): SyncReport {
        log.debug { "sync()" }

        return SyncReport(
            partners = partnerSyncer.sync(),
            finishedActivities = finishedActivitySyncer.sync()
        )
    }

}

data class SyncReport(
    val partners: PartnerSyncReport,
    val finishedActivities: FinishedActivitySyncReport
)
