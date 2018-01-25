package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.domain.activity.ActivityType
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.ActivityMyc
import com.github.christophpickl.urclubs.myclubs.CourseFilter
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import java.time.LocalDateTime
import javax.inject.Inject


class DataEnhancer @Inject constructor(
        private val myclubs: MyClubsApi,
        private val partnerService: PartnerService
) {
    fun todaysCourses(): List<EnhancedCourse> {
        val courses = myclubs.courses(CourseFilter(
                start = LocalDateTime.now().minusHours(2),
                end = LocalDateTime.now()
        ))
        return courses.map { course ->
            val activity = myclubs.activity(ActivityFilter(
                    activityId = course.id,
                    timestamp = course.timestamp,
                    type = ActivityType.Course
            ))
            val partner = partnerService.findByShortNameOrThrow(activity.partnerShortName)
            EnhancedCourse(
                    activity = activity,
                    partner = partner
            )
        }
    }
}

data class EnhancedCourse(
        val activity: ActivityMyc,
        val partner: Partner
)
