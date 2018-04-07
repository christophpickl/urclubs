package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner

class VisitsFilterSpec(private val view: FilterPartnersView) : FilterSpec {

    private val log = LOG {}
    private val visitsPredicate get() = view.visits.predicateProperty.get()

    override val isIrrelevant: Boolean get() = visitsPredicate == VisitsInputParser.anyPredicate

    override fun register(trigger: FilterTrigger) {
        view.visits.predicateProperty.addListener { _ ->
            log.trace { "Visits filter changed" }
            trigger.filter()
        }
    }

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        if (visitsPredicate != VisitsInputParser.anyPredicate) {
            predicates += visitsPredicate
        }
    }

}

object VisitsInputParser {

    private val log = LOG {}
    val anyPredicate = VisitFilterPredicate({ true })

    fun parse(rawInput: String): VisitFilterPredicate? {
        log.trace { "parse(rawInput='$rawInput')" }

        val input = rawInput.replace(" ", "")
        if (input.isEmpty()) {
            return anyPredicate
        }
        try {
            evalScript(input, "=") { filter, value -> filter == value }?.let { return it }
            input.toIntOrNull()?.let { return VisitFilterPredicate { testee -> it == testee } }
            evalScript(input, "!=") { filter, value -> filter != value }?.let { return it }

            evalScript(input, ">=") { filter, value -> filter >= value }?.let { return it }
            evalScript(input, ">") { filter, value -> filter > value }?.let { return it }

            evalScript(input, "<=") { filter, value -> filter <= value }?.let { return it }
            evalScript(input, "<") { filter, value -> filter < value }?.let { return it }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun evalScript(input: String, pattern: String, test: (Int, Int) -> Boolean): VisitFilterPredicate? {
        if (!input.startsWith(pattern)) {
            return null
        }
        val leftOver = input.substring(pattern.length)
        if (leftOver.isEmpty()) {
            return null
        }
        val leftOverInt = leftOver.toIntOrNull() ?: return null
        return VisitFilterPredicate({ testee -> test(testee, leftOverInt) })
    }

}

data class VisitFilterPredicate(private val check: (Int) -> Boolean) : FilterPredicate {
    override fun test(t: Partner) = check(t.totalVisits)
}
