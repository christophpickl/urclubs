package com.github.christophpickl.urclubs.testInfra

import org.assertj.core.api.Assertions
import org.assertj.core.api.ListAssert

fun <E> assertSingleElement(list: List<E>, expected: E) {
    Assertions.assertThat(list).hasSize(1)
    Assertions.assertThat(list[0]).isEqualTo(expected)
}

inline fun <reified ELEMENT> ListAssert<ELEMENT>.containsExactly(elements: List<ELEMENT>) {
    containsExactly(*elements.toTypedArray())
}
