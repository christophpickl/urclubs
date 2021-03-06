package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.domain.HasOrder
import com.github.christophpickl.urclubs.domain.OrderedEnumCompanion
import com.github.christophpickl.urclubs.domain.OrderedEnumCompanion2
import com.github.christophpickl.urclubs.domain.activity.FinishedActivity
import com.google.common.base.MoreObjects
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicInteger

typealias PartnerIntExtractor = Partner.() -> Int

data class Partner(
    val idDbo: Long,
    val idMyc: String, // "JYSvEcpVCR"

    val shortName: String, // "triller-crossfit" ... used for links
    val name: String, // "Triller CrossFit"
    val note: String,
    val linkMyclubs: String,
    val linkPartner: String?,

    val maxCredits: Int,
    val rating: Rating,
    val category: Category,
    val picture: PartnerImage,

    val favourited: Boolean,
    val wishlisted: Boolean, // want to go there soon (nevertheless whether i've been there already)
    val ignored: Boolean, // kind-a delete (don't display at all anymore anywhere, but keep in DB)
    val dateInserted: LocalDateTime,
    val dateDeleted: LocalDateTime?,

    val tags: List<String>,
    val addresses: List<String>,
    val finishedActivities: List<FinishedActivity>
) {

    val tagsFormatted: String = if (tags.isEmpty()) "-" else tags.joinToString()

    val visitsThisMonth: Int
    val creditsLeftThisPeriod: Int
    val totalVisits = finishedActivities.size
    val lastVisitInDays: Int?

    init {
        lastVisitInDays = if (finishedActivities.none { it != FinishedActivity.artificialInstance }) {
            null
        } else {
            Duration.between(
                finishedActivities.sortedBy { it.date }.last().date,
                LocalDateTime.now()
            ).toDays().toInt()
        }

        val now = LocalDateTime.now()
        visitsThisMonth = finishedActivities.filter {
            it.date.year == now.year && it.date.monthValue == now.monthValue
        }.size
        creditsLeftThisPeriod = maxCredits - visitsThisMonth
    }

    companion object {
        const val DEFAULT_MAX_CREDITS = 4
        const val DEFAULT_MAX_CREDITS_EMS = 2

        fun prototype() = Partner(
            idDbo = 0,
            idMyc = "",
            shortName = "",
            name = "",
            note = "",
            rating = Rating.UNKNOWN,
            maxCredits = DEFAULT_MAX_CREDITS,
            dateInserted = LocalDateTime.now(),
            dateDeleted = null,
            favourited = false,
            wishlisted = false,
            ignored = false,
            category = Category.UNKNOWN,
            linkMyclubs = "",
            linkPartner = null,
            addresses = emptyList(),
            tags = emptyList(),
            finishedActivities = emptyList(),
            picture = PartnerImage.DefaultPicture
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
                note = "my note 1",
                linkMyclubs = "http://orf.at",
                linkPartner = "http://google.com",
                category = Category.EMS,
                rating = Rating.SUPERB,
                maxCredits = 2,
                favourited = true,
                wishlisted = true,
                addresses = listOf("Passauer Pl. 2, 1010 Wien"),
                tags = listOf("EMS"),
                finishedActivities = listOf(
                    FinishedActivity("past", LocalDateTime.now().minusMonths(1)),
                    FinishedActivity("current", LocalDateTime.now())
                )
            )
        }

        val goodYoga = newDummy {
            copy(
                shortName = "yoga",
                name = "Dr. Yoga",
                linkMyclubs = "http://derstandard.at",
                category = Category.YOGA,
                rating = Rating.GOOD,
                addresses = listOf("Mieterstrasse 127/42, 1010 Wien"),
                tags = listOf("Yoga", "Bikram Yoga"),
                finishedActivities = listOf(
                    FinishedActivity("current1", LocalDateTime.now()),
                    FinishedActivity("current2", LocalDateTime.now()),
                    FinishedActivity("current3", LocalDateTime.now()),
                    FinishedActivity("current4", LocalDateTime.now())
                )
            )
        }
        val meh = newDummy {
            copy(
                shortName = "meh",
                name = "Meeeeh",
                addresses = listOf("add1", "add2", "add3"),
                rating = Rating.OK,
                favourited = true,
                finishedActivities = listOf(
                    FinishedActivity("past", LocalDateTime.now().minusMonths(1))
                )
            )
        }
        val badAss = newDummy {
            copy(
                shortName = "bad",
                name = "Bad Ass",
                addresses = listOf("doublette", "doublette"),
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
        val wannaGo = newDummy {
            copy(
                shortName = "wannaGo",
                name = "Might be Gooood",
                wishlisted = true,
                category = Category.WUSHU,
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
        .add("idMyc", idMyc)
        .add("shortName", shortName)
        .add("name", name)
        .add("rating", rating)
        .add("category", category)
        .add("maxCredits", maxCredits)
        .add("favourited", favourited)
        .add("wishlisted", wishlisted)
        .add("dateInserted", dateInserted)
        .add("dateDeleted", dateDeleted)
        .add("addresses", addresses)
        .add("ignored", ignored)
        .add("finishedActivities.size", finishedActivities.size)
        .add("picture", picture::class.java.simpleName)

        .toString()
}


enum class Rating(
    val label: String,
    val intValue: Int,
    override val order: Int
) : HasOrder {
    UNKNOWN("-UNKNOWN-", -1, 0),
    SUPERB("Superb", 3, 100),
    GOOD("Good", 2, 110),
    OK("Ok", 1, 120),
    BAD("Bad", 0, 130);

    object Ordered : OrderedEnumCompanion<Rating>(Rating.values())

    companion object {
        val maxIntValue = SUPERB.intValue
        val minIntValue = UNKNOWN.intValue
    }
}

enum class Category(
    val label: String
) {
    UNKNOWN("-UNKNOWN-"),
    DANCE("Dance"),
    GYM("Gym"),
    EMS("EMS"),
    HEALTH("Health"),
    PILATES("Pilates"),
    SPORT("Sport"),
    WATER("Wasser"),
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
