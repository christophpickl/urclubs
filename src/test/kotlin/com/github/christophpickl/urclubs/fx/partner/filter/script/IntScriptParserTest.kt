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

    private val extractTotalVisitsFromPartner = { it: Partner -> it.totalVisits }

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

    fun `Given minValue of 2`() {
        val parser = parser(IntScriptParserConfig.empty.copy(minValue = 2))

        assertThat(parser.parse("")).isAnyPredicate()
        assertThat(parser.parse("1")).isNull()
        assertThat(parser.parse(">1")).isNotNull
        assertThat(parser.parse("2")).isNotNull
        assertThat(parser.parse("=2")).isNotNull
        assertThat(parser.parse("3")).isNotNull
        assertThat(parser.parse("<3")).isNotNull

        // invalid operator
        assertThat(parser.parse("<=2")).isNull()
        assertThat(parser.parse("<2")).isNull()
    }

    fun `Given maxValue of 2`() {
        val parser = parser(IntScriptParserConfig.empty.copy(maxValue = 2))

        assertThat(parser.parse("")).isAnyPredicate()
        assertThat(parser.parse("1")).isNotNull
        assertThat(parser.parse(">1")).isNotNull
        assertThat(parser.parse("2")).isNotNull
        assertThat(parser.parse("!2")).isNotNull
        assertThat(parser.parse("3")).isNull()
        assertThat(parser.parse("<3")).isNotNull

        // invalid operator
        assertThat(parser.parse(">=2")).isNull()
        assertThat(parser.parse(">2")).isNull()
        assertThat(parser.parse("<=3")).isNull()
    }

    // domain

    private fun parser(config: IntScriptParserConfig = IntScriptParserConfig.empty) = IntScriptParser(
        intExtractor = extractTotalVisitsFromPartner,
        config = config
    )

    private fun parse(input: String, config: IntScriptParserConfig = IntScriptParserConfig.empty) = parser().parse(input)

    private fun partner(visits: Int) = Partner.testInstance.copy(finishedActivities = IntRange(1, visits).map { FinishedActivity.testInstance })

    // test infra

    private fun <T> PredicateAssert<T>.isAnyPredicate() {
        isSameAs(Filter.anyPredicate)
    }

    private fun assertInvalid(input: String, config: IntScriptParserConfig = IntScriptParserConfig.empty) {
        val predicate = parse(input, config)

        assertThat(predicate).isNull()
    }

    private fun FilterPredicate.assertPredicateMatches(visits: Int, expected: Boolean) {
        assertThat(test(partner(visits))).isEqualTo(expected)
    }

    private inline fun withPredicate(input: String, func: FilterPredicate.() -> Unit) {
        val predicate = parse(input)

        assertThat(predicate).isNotNull
        func(predicate!!)
    }

}
