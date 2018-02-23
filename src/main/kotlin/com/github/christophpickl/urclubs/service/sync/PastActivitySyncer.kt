package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import javax.inject.Inject

class PastActivitySyncer @Inject constructor(
        private val myclubs: MyClubsApi
) {

    private val log = LOG {}

    fun sync(): PastActivitySyncReport {
        log.info { "sync()" }
//        val activities = myclubs.finishedActivities()
        // FIXME implement sync past activities
        return PastActivitySyncReport("")
    }
}

data class PastActivitySyncReport(
        val foo: String
)
