package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Partner
import java.util.function.Predicate

sealed class Filter {

    companion object {
        val anyPredicate = object : FilterPredicate {
            override fun test(t: Partner) = true
        }
    }

    object NoFilter : Filter() {
        val all = { _: Partner -> true }
    }

    data class SomeFilter(private val predicates: List<FilterPredicate>) : Filter() {
        init {
            assert(predicates.isNotEmpty(), { "At least one predicate must be set, otherwise use NoFilter instead." })
        }

        fun concatPredicates() = { partner: Partner ->
            predicates.all { predicate ->
                predicate.test(partner)
            }
        }
    }
}

interface FilterPredicate : Predicate<Partner>

data class SimpleFilterPredicate(private val check: (Partner) -> Boolean) : FilterPredicate {
    override fun test(partner: Partner) = check(partner)
}

interface FilterSpec {
    val isIrrelevant: Boolean
    fun register(trigger: FilterTrigger)
    fun addToPredicates(predicates: MutableList<FilterPredicate>)
}

interface FilterTrigger {
    fun filter()
}
