package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerIntExtractor
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.fx.partner.filter.SimpleFilterPredicate

data class IntScriptParserConfig(
    val minValue: Int? = null,
    val maxValue: Int? = null
) {
    fun isValid(input: Int): Boolean {
        minValue?.let { min ->
            if (input < min) {
                return false
            }
        }
        maxValue?.let { max ->
            if (input > max) {
                return false
            }
        }
        return true
    }

    companion object {
        val empty = IntScriptParserConfig()
    }
}

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
            evalScript(input, "=") { filter, value -> filter == value }?.let { return it }
            // just a single number
            input.toIntOrNull()?.let { _ -> evalScriptWithRemovedOperator(input, { filter, value -> filter == value })?.let { return it } }
            evalScript(input, "!") { filter, value -> filter != value }?.let { return it }

            evalScript(input, ">=") { filter, value -> filter >= value }?.let { return it }
            evalScript(input, ">") { filter, value -> filter > value }?.let { return it }

            evalScript(input, "<=") { filter, value -> filter <= value }?.let { return it }
            evalScript(input, "<") { filter, value -> filter < value }?.let { return it }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun evalScript(input: String, operator: String, test: (Int, Int) -> Boolean): FilterPredicate? {
        if (!input.startsWith(operator)) {
            return null
        }
        val inputAfterOperator = input.substring(operator.length)
        return evalScriptWithRemovedOperator(inputAfterOperator, test)
    }

    private fun evalScriptWithRemovedOperator(inputAfterPattern: String, test: (Int, Int) -> Boolean): FilterPredicate? {
        if (inputAfterPattern.isEmpty()) {
            return null
        }
        val inputInt = inputAfterPattern.toIntOrNull() ?: return null
        if (!config.isValid(inputInt)) {
            return null
        }
        return SimpleFilterPredicate({ testee -> test(testee.intExtractor(), inputInt) })
    }

}

enum class FilterOperator(
    val operator: String,
    val test: (Int, Int) -> Boolean
) {
    Equals("=", { filter, value -> filter == value })
}
