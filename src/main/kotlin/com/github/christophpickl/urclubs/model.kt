package com.github.christophpickl.urclubs

interface HasOrder {
    val order: Int
}

abstract class OrderedEnumCompanion<out E : HasOrder>(values: Array<E>) {
    val allOrdered by lazy { values.toMutableList().apply { sortBy { it.order } }.toList() }
}
