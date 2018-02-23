package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javax.inject.Inject

class SyncService @Inject constructor(
        private val partnerSyncer: PartnerSyncer,
        private val pastActivitySyncer: PastActivitySyncer,
        private val upcomingActivitySyncer: UpcomingActivitySyncer
) {

    private val log = LOG {}

    fun sync(): PartnerSyncReport {
        log.debug { "sync()" }
        return partnerSyncer.sync()
    }
}
