package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.collection.toPrettyString
import com.github.christophpickl.urclubs.domain.activity.ActivityType
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.CourseFilter
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.service.Syncer
import com.github.christophpickl.urclubs.service.SyncerReport
import com.google.common.eventbus.EventBus
import java.time.LocalDateTime
import javax.inject.Inject

class App @Inject constructor(
        private val syncer: Syncer,
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService,
        private val bus: EventBus
) {
    fun start() {
//        println(syncer.sync().toPrettyString())
        myclubsPlayground()

//       val partnersMyc = myclubs.partners()

//       partnerService.create(Partner(idDbo = 0L, name = "foobar"))


        // Partner(idDbo=118, idMyc=yzWykzkxDX, name=Schwimmschule Wien, rating=UNKNOWN)
//        val schwimmschule = partnerService.read(id = 118)!!
//        partnerService.update(schwimmschule.copy(rating = Rating.SUPERB))

//        partnerService.readAll().prettyPrint()

//       myclubs.activities().prettyPrint()
        bus.post(QuitEvent)
    }

    private fun myclubsPlayground() {
        myclubs.login()
        val courses = myclubs.courses(CourseFilter(
                start = LocalDateTime.now().minusHours(2),
                end = LocalDateTime.now()
        ))
        if (courses.isNotEmpty()) {
            val course = courses[0]
            println(course)
            val activity = myclubs.activity(ActivityFilter(
                    activityId = course.id,
                    timestamp = course.timestamp,
                    type = ActivityType.Course
            ))
            println(activity)
            val partner = partnerService.findByShortName(activity.partnerShortName)
            println(partner)
        }
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
