package com.github.christophpickl.urclubs.service

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.Stopwatch
import com.github.christophpickl.urclubs.domain.activity.ActivityType
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.ActivityFilter
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.ActivityHtmlModel
import com.github.christophpickl.urclubs.myclubs.parser.CourseHtmlModel
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject

class CourseEnhancer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val partnerService: PartnerService
) {

    private val log = LOG {}

    fun todaysCourses(courses: List<CourseHtmlModel>): List<EnhancedCourse> {
        log.debug { "todaysCourses(courses)" }
        return Stopwatch.elapse("Enhancing ${courses.size} courses") {
            enhanceCourses(courses)
        }
    }

    private fun enhanceCourses(courses: List<CourseHtmlModel>): List<EnhancedCourse> =
        runBlocking {
            courses.map { course ->
                async {
                    enhanceCourse(course)
                }
            }.map { it.await() }
        }

    private fun enhanceCourse(course: CourseHtmlModel): EnhancedCourse {
        log.trace { "enhanceCourse(course=$course)" }
        val activity = myclubs.activity(ActivityFilter(
            activityId = course.id,
            timestamp = course.timestamp,
            type = ActivityType.Course
        ))
        val partner = partnerService.findByShortNameOrThrow(activity.partnerShortName)
        return EnhancedCourse(
            activity = activity,
            partner = partner
        )
    }
}

data class EnhancedCourse(
    val activity: ActivityHtmlModel,
    val partner: Partner
)


