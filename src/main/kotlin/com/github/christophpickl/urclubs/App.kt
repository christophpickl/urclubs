package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.CourseEnhancer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.github.christophpickl.urclubs.service.sync.PastActivitySyncer
import com.google.common.eventbus.EventBus
import javax.inject.Inject

class App @Inject constructor(
        private val partnerSyncer: PartnerSyncer,
        private val pastActivitySyncer: PastActivitySyncer,
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService,
        private val bus: EventBus,
        private val enhancer: CourseEnhancer
) {
    fun start() {
        myclubs.login() // TODO refactor that always login at startup

        playground()

        bus.post(QuitEvent)
    }

    private fun playground() {
        //pastActivitySyncer.sync()
        myclubs.finishedActivities().prettyPrint()
    }

}
