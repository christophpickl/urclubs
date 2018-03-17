package com.github.christophpickl.urclubs.service.sync

import com.github.christophpickl.kpotpourri.common.collection.prettyPrint
import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.DEVELOPMENT_FAST_SYNC
import com.github.christophpickl.urclubs.domain.activity.ActivityService
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.parser.FinishedActivityHtmlModel
import javax.inject.Inject

class FinishedActivitySyncer @Inject constructor(
    private val myclubs: MyClubsApi,
    private val activityService: ActivityService,
    private val partnerService: PartnerService
) {

    private val log = LOG {}

    fun sync(): FinishedActivitySyncReport {
        log.info { "sync()" }
        val activitiesFetched = fetchFinishedActivities()
        activitiesFetched.prettyPrint()
        val activitiesStored = activityService.readAllFinished()

//        val fetchedById = activitiesFetched.associateBy { it.activity. }
//        val fetchedIds = fetchedById.keys
//        val storedById = partnersStored.associateBy { it.idMyc }
//        val storedIds = storedById.keys
//
//        val insertedPartners = fetchedById.minus(storedIds).values.map { partnerMyc ->
//            val rawPartner = partnerMyc.toPartner()
//            val detailPartner = myclubs.partner(rawPartner.shortName)
//            val partner = rawPartner.enhance(detailPartner)
//            partnerService.create(partner)
//        }
//
//        val deletedPartners = storedById.minus(fetchedIds).values
//            .map { it.copy(deletedByMyc = true) }
//            .apply {
//                forEach {
//                    partnerService.update(it)
//                }
//            }.toList()

        // FIXME implement sync past activities

        return FinishedActivitySyncReport(inserted = emptyList(), deleted = emptyList())
    }
    private fun fetchFinishedActivities(): List<EnhancedFinishedActivity> {
        val activities = myclubs.finishedActivities().run {
            if (DEVELOPMENT_FAST_SYNC) take(1) else this
        }
        return activities.map { activity ->
            EnhancedFinishedActivity(
                activity = activity,
                partner = partnerService.searchPartner(activity.locationHtml)
                    ?: throw Exception("Could not find partner for $activity")
            )
        }
    }

}

private data class EnhancedFinishedActivity(
    val activity: FinishedActivityHtmlModel,
    val partner: Partner
)

data class FinishedActivitySyncReport(
    val inserted: List<Any>,
    val deleted: List<Any>
)
