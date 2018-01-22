package com.github.christophpickl.urclubs.testInfra

import org.assertj.core.api.AbstractObjectAssert
import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert
import org.assertj.core.api.ObjectAssert
import kotlin.reflect.KProperty1

// MINOR create assert4k library

fun <E> assertThatSingleElement(list: List<E>, expected: E) {
    Assertions.assertThat(list).hasSize(1)
    Assertions.assertThat(list[0]).isEqualTo(expected)
}

inline fun <reified ELEMENT> ListAssert<ELEMENT>.containsExactly(elements: List<ELEMENT>) {
    containsExactly(*elements.toTypedArray())
}

fun <T> ObjectAssert<T>.isEqualToIgnoringGivenProps(expected: T, vararg propsToIgnore: KProperty1<T, Any?>) {
    isEqualToIgnoringGivenFields(expected, *propsToIgnore.map { it.name }.toTypedArray())
}

fun <T> AbstractObjectAssert<*, T>.isEqualToIgnoringGivenProps(expected: T, vararg propsToIgnore: KProperty1<T, Any?>) {
    isEqualToIgnoringGivenFields(expected, *propsToIgnore.map { it.name }.toTypedArray())
}

fun <T> ListAssert<T>.singleEntryIsEqualToIgnoringGivenProps(expected: T, vararg propsToIgnore: KProperty1<T, Any?>) {
    hasSize(1)
    element(0).isEqualToIgnoringGivenProps(expected, *propsToIgnore)
}

