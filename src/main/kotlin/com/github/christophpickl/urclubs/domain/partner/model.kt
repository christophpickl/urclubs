package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.HasOrder
import com.github.christophpickl.urclubs.OrderedEnumCompanion
import com.github.christophpickl.urclubs.OrderedEnumCompanion2
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
    val maxCredits: Int,
    val deletedByMyc: Boolean, // keep in DB still locally
    val favourited: Boolean,
    val wishlisted: Boolean, // want to go there soon (nevertheless whether i've been there already)
    val ignored: Boolean, // kind-a delete (don't display at all anymore anywhere, but keep in DB)
    val category: Category,
    val linkMyclubs: String,
    val linkPartnerSite: String,
    val picture: Picture
) {

    companion object {
        const val DEFAULT_MAX_CREDITS = 4

        fun prototype() = Partner(
            idDbo = 0,
            idMyc = "",
            shortName = "",
            name = "",
            note = "",
            address = "",
            rating = Rating.UNKNOWN,
            maxCredits = DEFAULT_MAX_CREDITS,
            deletedByMyc = false,
            favourited = false,
            wishlisted = false,
            ignored = false,
            category = Category.UNKNOWN,
            linkMyclubs = "",
            linkPartnerSite = "",
            picture = Picture.DefaultPicture
        )
    }

    object Dummies {
        private val counter = AtomicInteger()
        private val allMutable = mutableListOf<Partner>()
        val all: List<Partner> get() = allMutable

        val superbEms = newDummy {
            copy(
                shortName = "dummy-ems",
                name = "Dummy EMS",
                address = "Hauptplatz 1",
                note = "my note 1",
                linkMyclubs = "http://orf.at",
                linkPartnerSite = "http://google.com",
                category = Category.EMS,
                rating = Rating.SUPERB,
                maxCredits = 2,
                favourited = true,
                wishlisted = true
            )
        }

        val goodYoga = newDummy {
            copy(
                shortName = "yoga",
                name = "Dr. Yoga",
                address = "Mieterstrasse 127/42, 1010 Wien",
                linkMyclubs = "http://derstandard.at",
                category = Category.YOGA,
                rating = Rating.GOOD
            )
        }
        val mahOk = newDummy {
            copy(
                shortName = "mah",
                name = "Maaaah",
                rating = Rating.OK
            )
        }
        val badAss = newDummy {
            copy(
                shortName = "bad",
                name = "Bad Ass",
                rating = Rating.BAD
            )
        }
        val mrUnknown = newDummy {
            copy(
                shortName = "unknown",
                name = "Mr Unknown",
                category = Category.UNKNOWN,
                rating = Rating.UNKNOWN
            )
        }
        val ignored = newDummy {
            copy(
                shortName = "ignored",
                name = "Ignored one",
                category = Category.OTHER,
                rating = Rating.BAD,
                ignored = true
            )
        }

        private fun newDummy(action: Partner.() -> Partner) = action(prepare()).also { allMutable += it }

        private fun prepare(): Partner {
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
    val label: String,
    override val order: Int
) : HasOrder {
    UNKNOWN("-UNKNOWN-", 0),
    SUPERB("Superb", 100),
    GOOD("Good", 110),
    OK("Ok", 120),
    BAD("Bad", 130);

    object Ordered : OrderedEnumCompanion<Rating>(Rating.values())
}

enum class Category(
    val label: String
) {
    UNKNOWN("-UNKNOWN-"),
    GYM("Gym"),
    EMS("EMS"),
    HEALTH("Health"),
    WORKOUT("Workout"),
    WUSHU("Wushu"),
    YOGA("Yoga"),
    OTHER("Other");

    companion object {
        private val defaultComparator = Comparator<Category> { o1, o2 ->
            if (o1 == UNKNOWN && o2 != UNKNOWN) {
                -1
            } else if (o1 != UNKNOWN && o2 == UNKNOWN) {
                1
            } else {
                o1.label.compareTo(o2.label)
            }
        }
    }

    object Ordered : OrderedEnumCompanion2<Category>(Category.values(), defaultComparator)

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
    maxCredits = maxCredits.toByte(),
    picture = picture.saveRepresentation,
    linkMyclubs = linkMyclubs,
    linkPartner = linkPartnerSite
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
    maxCredits = maxCredits.toInt(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategory(),
    linkMyclubs = linkMyclubs,
    linkPartnerSite = linkPartner,
    picture = Picture.readFromDb(picture)
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
