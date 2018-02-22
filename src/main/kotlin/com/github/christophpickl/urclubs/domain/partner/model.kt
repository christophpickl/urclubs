package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.HasOrder
import com.github.christophpickl.urclubs.OrderedEnumCompanion
import com.google.common.base.MoreObjects

data class Partner(
    val idDbo: Long,
    val idMyc: String, // "JYSvEcpVCR"
    val shortName: String, // "triller-crossfit" ... used for links
    val name: String, // "Triller CrossFit"
    val address: String,
    val rating: Rating,
    val deletedByMyc: Boolean, // keep in DB still locally
    val favourited: Boolean,
    val wishlisted: Boolean, // want to go there soon (nevertheless whether i've been there already)
    val ignored: Boolean, // kind-a delete (don't display at all anymore anywhere, but keep in DB)
    val category: Category,
    val linkMyclubsSite: String,
    val linkPartnerSite: String

    // categoryMyc ?

// maybe introduce myclubsMetadata object??
) {
    companion object {}

    override fun toString() = MoreObjects.toStringHelper(this)
            .add("idDbo", idDbo)
            .add("shortName", shortName)
            .add("name", name)
            .toString()
}

enum class Rating(
    override val order: Int
) : HasOrder {
    UNKNOWN(0),
    BAD(1),
    OK(2),
    GOOD(3),
    SUPERB(4);

    object Ordered : OrderedEnumCompanion<Rating>(Rating.values())
}

enum class Category(
    val label: String
) {
    EMS("EMS"),
    GYM("Gym"),
    YOGA("Yoga"),
    WUSHU("Wushu"),
    WORKOUT("Workout"),
    HEALTH("Health"),
    OTHER("Other"),
    UNKNOWN("Unknown")
}
