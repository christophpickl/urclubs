package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerIntExtractor
import com.github.christophpickl.urclubs.fx.partner.filter.Filter
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.fx.partner.filter.SimpleFilterPredicate

class IntScriptParser(
    private val intExtractor: PartnerIntExtractor
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
            input.toIntOrNull()?.let { inputInt -> return SimpleFilterPredicate { testee -> inputInt == testee.intExtractor() } }
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

    private fun evalScript(input: String, pattern: String, test: (Int, Int) -> Boolean): FilterPredicate? {
        if (!input.startsWith(pattern)) {
            return null
        }
        val inputAfterPattern = input.substring(pattern.length)
        if (inputAfterPattern.isEmpty()) {
            return null
        }
        val inputInt = inputAfterPattern.toIntOrNull() ?: return null
        return SimpleFilterPredicate({ testee -> test(testee.intExtractor(), inputInt) })
    }

}
