package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerIntExtractor
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.fx.partner.filter.SimpleFilterPredicate

class IntScriptParser(
    private val intExtractor: PartnerIntExtractor,
    private val config: IntScriptParserConfig = IntScriptParserConfig.empty
) {

    private val log = LOG {}

    fun parse(rawInput: String): FilterPredicate? {
        log.trace { "parse(rawInput='$rawInput')" }

        val input = rawInput.replace(" ", "")
        if (input.isEmpty()) {
            return Filter.anyPredicate
        }
        try {
            // just a single number
            input.toIntOrNull()?.let { _ -> evalScriptWithRemovedOperator(input, FilterOperation.Equals)?.let { return it } }

            FilterOperation.values().forEach {
                evalScript(input, it)?.let { return it }
            }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun evalScript(input: String, operation: FilterOperation): FilterPredicate? {
        if (!input.startsWith(operation.operator)) {
            return null
        }
        val inputAfterOperator = input.substring(operation.operator.length)
        return evalScriptWithRemovedOperator(inputAfterOperator, operation)
    }

    private fun evalScriptWithRemovedOperator(inputAfterPattern: String, operation: FilterOperation): FilterPredicate? {
        if (inputAfterPattern.isEmpty()) {
            return null
        }
        val inputInt = inputAfterPattern.toIntOrNull() ?: return null
        if (!config.isValid(inputInt, operation)) {
            return null
        }
        return SimpleFilterPredicate({ testee -> operation.test(testee.intExtractor(), inputInt) })
    }

}

data class IntScriptParserConfig(
    val minValue: Int? = null,
    val maxValue: Int? = null
) {
    fun isValid(input: Int, operation: FilterOperation): Boolean {
        minValue?.let { min ->
            if (input < min && (operation != FilterOperation.Bigger || input != min - 1)) {
                return false
            }
            if (input == min && (operation == FilterOperation.Lower || operation == FilterOperation.LowerEquals)) {
                return false
            }
        }
        maxValue?.let { max ->
            if (input > max && (operation != FilterOperation.Lower || input != max + 1)) {
                return false
            }
            if (input == max && (operation == FilterOperation.Bigger || operation == FilterOperation.BiggerEquals)) {
                return false
            }
        }
        return true
    }

    companion object {
        val empty = IntScriptParserConfig()
    }
}

enum class FilterOperation(
    val operator: String,
    val test: (Int, Int) -> Boolean
) {
    // @formatter:off
    Equals(      "=",  { filter, value -> filter == value }),
    NotEquals(   "!",  { filter, value -> filter != value }),
    LowerEquals( "<=", { filter, value -> filter <= value }),
    Lower(       "<",  { filter, value -> filter <  value }),
    BiggerEquals(">=", { filter, value -> filter >= value }),
    Bigger(      ">",  { filter, value -> filter >  value })
    // @formatter:on
}
