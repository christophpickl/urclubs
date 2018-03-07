package com.github.christophpickl.urclubs

interface HasOrder {
    val order: Int
}

abstract class OrderedEnumCompanion<out E : HasOrder>(values: Array<E>) {
    val allOrdered: List<E> by lazy { values.toMutableList().apply { sortBy { it.order } } }
}

abstract class OrderedEnumCompanion2<E : Comparable<E>>(values: Array<E>, comparator: Comparator<E>) {
    val allOrdered: List<E> by lazy { values.toMutableList().run { sortedWith(comparator) } }
}
