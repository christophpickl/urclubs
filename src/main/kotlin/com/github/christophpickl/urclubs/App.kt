package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.DataEnhancer
import com.github.christophpickl.urclubs.service.Syncer
import com.github.christophpickl.urclubs.service.SyncerReport
import com.google.common.eventbus.EventBus
import javax.inject.Inject

class App @Inject constructor(
        private val syncer: Syncer,
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService,
        private val bus: EventBus,
        private val enhancer: DataEnhancer
) {
    fun start() {
        myclubs.login()

//        println(syncer.sync().toPrettyString())
//        myclubsPlayground()

//       val partnersMyc = myclubs.partners()

//       partnerService.create(Partner(idDbo = 0L, name = "foobar"))


        // Partner(idDbo=118, idMyc=yzWykzkxDX, name=Schwimmschule Wien, rating=UNKNOWN)
//        val schwimmschule = partnerService.read(id = 118)!!
//        partnerService.update(schwimmschule.copy(rating = Rating.SUPERB))

//        partnerService.readAll().prettyPrint()

        enhancer.todaysCourses().prettyPrint()
//       myclubs.activities().prettyPrint()
        bus.post(QuitEvent)
    }

}

private fun SyncerReport.toPrettyString() =
        """Sync Report:
==========================
Inserted (${insertedPartners.size}):
--------------------------
${insertedPartners.toPrettyString()}

Deleted(${deletedPartners.size}):
--------------------------
${deletedPartners.toPrettyString()}
"""
