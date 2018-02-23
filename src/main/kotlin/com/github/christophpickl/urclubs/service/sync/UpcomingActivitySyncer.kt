package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import javax.inject.Inject

class UpcomingActivitySyncer @Inject constructor(
        private val myclubs: MyClubsApi
) {

    private val log = LOG {}

    fun sync(): UpcomingActivitySyncReport {
        log.info { "sync()" }
        val partner = myclubs.partner("sporthalle-wien")
        partner.upcomingActivities.prettyPrint()
        // FIXME implement sync upcoming activities
        return UpcomingActivitySyncReport("")
    }
}
data class UpcomingActivitySyncReport(
        val foo: String
)
