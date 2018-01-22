package com.github.christophpickl.urclubs

data class Credentials(
        val email: String,
        val password: String
)

data class Partner(
        val idDbo: Long,
        val idMyc: String,
        val name: String,
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

interface HasOrder {
    val order: Int
}

abstract class OrderedEnumCompanion<out E : HasOrder>(values: Array<E>) {
    val allOrdered by lazy { values.toMutableList().apply { sortBy { it.order } }.toList() }
}
