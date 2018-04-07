package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.urclubs.domain.activity.FinishedActivity
import com.github.christophpickl.urclubs.domain.activity.testInstance
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.testInstance
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.PredicateAssert
import org.testng.annotations.Test

@Test
class IntScriptParserTest {

    private val extractFromPartner = { it: Partner -> it.totalVisits }

    fun `Given empty '' Then return any predicate`() {
        assertThat(parse("")).isAnyPredicate()
    }

    fun `Given whitespace ' ' Then return any predicate`() {
        assertThat(parse(" ")).isAnyPredicate()
    }

    fun `Given '= 0'`() {
        withPredicate("= 0") {
            assertPredicateMatches(0, true)
            assertPredicateMatches(1, false)
        }
    }

    fun `Given only '0' same as '= 0'`() {
        withPredicate("0") {
            assertPredicateMatches(0, true)
            assertPredicateMatches(1, false)
        }
    }

    fun `Given '! 0'`() {
        withPredicate("! 0") {
            assertPredicateMatches(0, false)
            assertPredicateMatches(1, true)
        }
    }

    fun `Given 'bigger= 2'`() {
        withPredicate(">= 2") {
            assertPredicateMatches(1, false)
            assertPredicateMatches(2, true)
            assertPredicateMatches(3, true)
        }
    }

    fun `Given 'bigger 2'`() {
        withPredicate("> 2") {
            assertPredicateMatches(1, false)
            assertPredicateMatches(2, false)
            assertPredicateMatches(3, true)
        }
    }

    fun `Given 'lower= 2'`() {
        withPredicate("<= 2") {
            assertPredicateMatches(1, true)
            assertPredicateMatches(2, true)
            assertPredicateMatches(3, false)
        }
    }

    fun `Given 'lower 2'`() {
        withPredicate("< 2") {
            assertPredicateMatches(1, true)
            assertPredicateMatches(2, false)
            assertPredicateMatches(3, false)
        }
    }

    fun `Given invalid '='`() {
        assertInvalid("=")
    }

    fun `Given invalid '=a'`() {
        assertInvalid("=a")
    }

    private inline fun withPredicate(input: String, func: FilterPredicate.() -> Unit) {
        val predicate = parse(input)

        assertThat(predicate).isNotNull
        func(predicate!!)
    }

    private fun assertInvalid(input: String) {
        val predicate = parse(input)

        assertThat(predicate).isNull()
    }

    private fun FilterPredicate.assertPredicateMatches(visits: Int, expected: Boolean) {
        assertThat(test(partner(visits))).isEqualTo(expected)
    }

    private fun partner(visits: Int) = Partner.testInstance.copy(finishedActivities = IntRange(1, visits).map { FinishedActivity.testInstance })

    private fun parse(input: String) = IntScriptParser(extractFromPartner).parse(input)

    private fun <T> PredicateAssert<T>.isAnyPredicate() {
        isSameAs(Filter.anyPredicate)
    }

}
