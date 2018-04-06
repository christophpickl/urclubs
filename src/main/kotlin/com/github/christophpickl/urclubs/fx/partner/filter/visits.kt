package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Partner

object VisitsInputParser {

    val anyScript = "any"
    val anyPredicate = VisitFilterPredicate({ true })

    fun parse(input: String): VisitFilterPredicate? {
        val trimmed = input.replace(" ", "")
        if (trimmed == anyScript) {
            return anyPredicate
        }
        try {
            evalScript(trimmed, "=") { filter, value -> filter == value }?.let { return it }
            evalScript(trimmed, "!=") { filter, value -> filter != value }?.let { return it }

            evalScript(trimmed, ">=") { filter, value -> filter >= value }?.let { return it }
            evalScript(trimmed, ">") { filter, value -> filter > value }?.let { return it }

            evalScript(trimmed, "<=") { filter, value -> filter <= value }?.let { return it }
            evalScript(trimmed, "<") { filter, value -> filter < value }?.let { return it }
        } catch (e: Exception) {
            return null
        }
        return null
    }

    private fun evalScript(trimmed: String, pattern: String, test: (Int, Int) -> Boolean): VisitFilterPredicate? {
        if (!trimmed.startsWith(pattern)) {
            return null
        }
        val leftOver = trimmed.substring(pattern.length)
        if (leftOver.isEmpty()) {
            return null
        }
        val leftOverInt = leftOver.toIntOrNull() ?: return null
        return VisitFilterPredicate({ test(it, leftOverInt) })
    }

}

data class VisitFilterPredicate(private val check: (Int) -> Boolean) : FilterPredicate {
    override fun test(t: Partner) = check(t.totalVisits)
}
