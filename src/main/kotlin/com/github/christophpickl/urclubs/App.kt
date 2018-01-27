package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.CourseEnhancer
import com.github.christophpickl.urclubs.service.FinishedActivityEnhancer
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
    private val courseEnhancer: CourseEnhancer,
    private val activityEnhancer: FinishedActivityEnhancer
) {
    fun start() {
        myclubs.login() // TODO refactor that always login at startup, use proper Connector to ensure state

        playground()

        bus.post(QuitEvent)
    }

    private fun playground() {
//        partnerSyncer.sync()
//        partnerService.readAll().prettyPrint()

//        pastActivitySyncer.sync()

//        myclubs.finishedActivities().prettyPrint()
//        println(myclubs.partner("hotpod-yoga-vienna"))

        val finished = myclubs.finishedActivities()
        activityEnhancer.enhance(finished.take(3)).prettyPrint()
    }

}
