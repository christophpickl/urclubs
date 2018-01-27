package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.HasOrder
import com.github.christophpickl.urclubs.OrderedEnumCompanion

data class Partner(
    val idDbo: Long,
    val idMyc: String, // "JYSvEcpVCR"
    val shortName: String, // "triller-crossfit" ... used for links
    val name: String, // "Triller CrossFit"
    val address: String,
    val rating: Rating,
    val deletedByMyc: Boolean
    // URL
    // category
    // categoryMyc ... maybe introduce myclubsMetadata object??
) {
    companion object {
        val dummies = listOf(
            Partner(idDbo = 1, idMyc = "myc1", shortName = "taiji", name = "Taiji", rating = Rating.GOOD, deletedByMyc = false, address = "Wienerstrasse 66, 1010 Wien"),
            Partner(idDbo = 2, idMyc = "myc2", shortName = "ems", name = "EMS", rating = Rating.OK, deletedByMyc = false, address = "Margaratenguertel 12, 1030 Wien")
        )
    }
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
