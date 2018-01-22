package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.HasOrder
import com.github.christophpickl.urclubs.OrderedEnumCompanion

data class Partner(
        val idDbo: Long,
        val idMyc: String, // "JYSvEcpVCR"
        val shortName: String, // "triller-crossfit" ... used for links
        val name: String, // "Triller CrossFit"
        val rating: Rating
)

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
