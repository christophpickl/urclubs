package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.HasOrder
import com.github.christophpickl.urclubs.OrderedEnumCompanion
import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo
import com.google.common.base.MoreObjects
import java.util.concurrent.atomic.AtomicInteger

data class Partner(
    val idDbo: Long,
    val idMyc: String, // "JYSvEcpVCR"
    val shortName: String, // "triller-crossfit" ... used for links
    val name: String, // "Triller CrossFit"
    val note: String,
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

    companion object {
        private val counter = AtomicInteger()

        fun prototype() = Partner(
            idDbo = 0,
            idMyc = "",
            shortName = "",
            name = "",
            note = "",
            address = "",
            rating = Rating.UNKNOWN,
            deletedByMyc = false,
            favourited = false,
            wishlisted = false,
            ignored = false,
            category = Category.UNKNOWN,
            linkMyclubsSite = "",
            linkPartnerSite = ""
        )

        val dummy1 =
            dummyPartner().copy(
                shortName = "dummy-ems",
                name = "Dummy EMS",
                address = "Hauptplatz 1",
                note = "my note 1",
                linkMyclubsSite = "http://www.orf.at",
                linkPartnerSite = "http://www.derstandard.at",
                category = Category.EMS,
                rating = Rating.SUPERB,
                favourited = true,
                wishlisted = true
            )

        val dummy2 = dummyPartner().copy(
            shortName = "dummy-yoga",
            name = "Dummy Yoga",
            address = "Mieterstrasse 127/42, 1010 Wien",
            category = Category.YOGA
        )
        val dummy3 = dummyPartner().copy(
            shortName = "mah",
            name = "Maaaah",
            category = Category.OTHER,
            rating = Rating.BAD,
            ignored = true
        )
        val dummies = listOf(dummy1, dummy2, dummy3)

        private fun dummyPartner(): Partner {
            val count = counter.incrementAndGet()
            return Partner.prototype().copy(
                idMyc = "dummy$count",
                shortName = "dummy$count"
            )
        }
    }

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
    UNKNOWN("Unknown"),
    EMS("EMS"),
    GYM("Gym"),
    HEALTH("Health"),
    OTHER("Other"),
    WORKOUT("Workout"),
    WUSHU("Wushu"),
    YOGA("Yoga")
}

// =====================================================================================================================
// PERSISTENCE TRANSFORMER
// =====================================================================================================================

fun Partner.toPartnerDbo() = PartnerDbo(
    id = idDbo,
    idMyc = idMyc,
    name = name,
    note = note,
    shortName = shortName,
    address = address,
    rating = rating.toRatingDbo(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategoryDbo(),
    linkMyclubsSite = linkMyclubsSite,
    linkPartnerSite = linkPartnerSite
)

fun Rating.toRatingDbo() = when (this) {
    Rating.UNKNOWN -> RatingDbo.UNKNOWN
    Rating.BAD -> RatingDbo.BAD
    Rating.OK -> RatingDbo.OK
    Rating.GOOD -> RatingDbo.GOOD
    Rating.SUPERB -> RatingDbo.SUPERB
}

fun Category.toCategoryDbo() = when (this) {
    Category.EMS -> CategoryDbo.EMS
    Category.GYM -> CategoryDbo.GYM
    Category.YOGA -> CategoryDbo.YOGA
    Category.WUSHU -> CategoryDbo.WUSHU
    Category.WORKOUT -> CategoryDbo.WORKOUT
    Category.HEALTH -> CategoryDbo.HEALTH
    Category.OTHER -> CategoryDbo.OTHER
    Category.UNKNOWN -> CategoryDbo.UNKNOWN
}

fun PartnerDbo.toPartner() = Partner(
    idDbo = id,
    idMyc = idMyc,
    shortName = shortName,
    name = name,
    note = note,
    address = address,
    rating = rating.toRating(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategory(),
    linkMyclubsSite = linkMyclubsSite,
    linkPartnerSite = linkPartnerSite
)

fun RatingDbo?.toRating() = when (this) {
    RatingDbo.UNKNOWN -> Rating.UNKNOWN
    RatingDbo.BAD -> Rating.BAD
    RatingDbo.OK -> Rating.OK
    RatingDbo.GOOD -> Rating.GOOD
    RatingDbo.SUPERB -> Rating.SUPERB
    null -> Rating.UNKNOWN
}

fun CategoryDbo?.toCategory() = when (this) {
    CategoryDbo.EMS -> Category.EMS
    CategoryDbo.GYM -> Category.GYM
    CategoryDbo.YOGA -> Category.YOGA
    CategoryDbo.WUSHU -> Category.WUSHU
    CategoryDbo.WORKOUT -> Category.WORKOUT
    CategoryDbo.HEALTH -> Category.HEALTH
    CategoryDbo.OTHER -> Category.OTHER
    CategoryDbo.UNKNOWN -> Category.UNKNOWN
    null -> Category.UNKNOWN
}
