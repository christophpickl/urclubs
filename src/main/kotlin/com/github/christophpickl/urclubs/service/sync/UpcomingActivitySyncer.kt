package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.activity.UpcomingActivity
import com.github.christophpickl.urclubs.myclubs.CourseFilter
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class UpcomingActivitySyncer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val activityService: ActivityService
) {

    private val log = LOG {}

    fun sync(): UpcomingActivitySyncReport {
        log.info { "sync()" }

        // 1: get upcoming activities via date range
        val filter = CourseFilter(
            start = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS),
            end = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS).withHour(23).withMinute(59)
        )
        log.debug { "Courses filter: $filter" }
        val fetchedCourses = myclubs.courses(filter)
        val storedCourses = activityService.readUpcoming(filter.start, filter.end)

        // FIXME compute diff of fetched / stored + insert
        val inserted = emptyList<UpcomingActivity>()

        return UpcomingActivitySyncReport(inserted)
    }
    // 2: OR get upcoming via partner
//        val partner = myclubs.partner("sporthalle-wien")
//        partner.upcomingActivities.prettyPrint()
}

data class UpcomingActivitySyncReport(
    val inserted: List<UpcomingActivity>
)
