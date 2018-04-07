package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG

class VisitsFilterSpec(private val view: FilterPartnersView) : FilterSpec {

    private val log = LOG {}
    private val visitsPredicate get() = view.visits.predicateProperty.get()

    override val isIrrelevant: Boolean get() = visitsPredicate == Filter.anyPredicate

    override fun register(trigger: FilterTrigger) {
        view.visits.predicateProperty.addListener { _ ->
            log.trace { "Visits filter changed" }
            trigger.filter()
        }
    }

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        if (visitsPredicate != null && visitsPredicate != Filter.anyPredicate) {
            predicates += visitsPredicate
        }
    }

}

object VisitsInputParser {

    private val log = LOG {}

    fun parse(rawInput: String): FilterPredicate? {
        log.trace { "parse(rawInput='$rawInput')" }

        val input = rawInput.replace(" ", "")
        if (input.isEmpty()) {
            return Filter.anyPredicate
        }
        try {
            evalScript(input, "=") { filter, value -> filter == value }?.let { return it }
            input.toIntOrNull()?.let { inputInt -> return SimpleFilterPredicate { testee -> inputInt == testee.totalVisits } }
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
        return SimpleFilterPredicate({ testee -> test(testee.totalVisits, inputInt) })
    }

}
