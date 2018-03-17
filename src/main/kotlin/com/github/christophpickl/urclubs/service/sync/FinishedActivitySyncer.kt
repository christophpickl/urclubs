package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.DEVELOPMENT_FAST_SYNC
import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.activity.FinishedActivity
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import javax.inject.Inject

fun FinishedActivityHtmlModel.toFinishedActivity() = FinishedActivity(
    title = title,
    date = date
)

class FinishedActivitySyncer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val activityService: ActivityService,
    private val partnerService: PartnerService
) {

    private val log = LOG {}

    // those partners have been deleted in the meanwhile by myclubs
    private val ignoredActivityLocations = listOf(
        "Bodystreet Convalere<br>Ungargasse 46, 1030 Wien",
        "MINDFUL_BODY WIEN<br>Paulanergasse 13, 1040 Wien",
        "Fitness Festival<br>Wipplingerstraße 30, 1010 Wien"
    )

    fun sync(): FinishedActivitySyncReport {
        log.info { "sync()" }
        val activitiesFetched = fetchFinishedActivities()
        val notYetInsertedActivities = filterNotYetInserted(activitiesFetched)

        val insertedActivities = mutableListOf<FinishedActivity>()
        notYetInsertedActivities.forEach { activity ->
            val finishedActivity = activity.activity.toFinishedActivity()
            insertedActivities += finishedActivity
            val partner = activity.partner.addFinishedActivity(finishedActivity)
            partnerService.update(partner)
        }

        return FinishedActivitySyncReport(inserted = insertedActivities)
    }

    private fun Partner.addFinishedActivity(activity: FinishedActivity) = copy(
        finishedActivities = finishedActivities.toMutableList().apply { add(activity) }
    )

    private fun filterNotYetInserted(fetched: List<EnhancedFinishedActivity>): List<EnhancedFinishedActivity> {
        val stored = activityService.readAllFinished()
        return fetched.filter {
            !stored.contains(it.activity.toFinishedActivity())
        }
    }

    private fun fetchFinishedActivities(): List<EnhancedFinishedActivity> {
        val activities = myclubs.finishedActivities().run {
            if (DEVELOPMENT_FAST_SYNC) take(5) else this
        }
        return activities.mapNotNull { activity ->
            val partner = partnerService.searchPartner(activity.locationHtml)
                ?: if (ignoredActivityLocations.contains(activity.locationHtml)) {
                    log.info { "Ignoring activity which is known to be associated by a deleted partner." }
                    return@mapNotNull null
                } else {
                    throw Exception("Could not find partner for: $activity")
                }
            EnhancedFinishedActivity(
                activity = activity,
                partner = partner
            )
        }
    }

}

private data class EnhancedFinishedActivity(
    val activity: FinishedActivityHtmlModel,
    val partner: Partner
)

data class FinishedActivitySyncReport(
    val inserted: List<FinishedActivity>
)
