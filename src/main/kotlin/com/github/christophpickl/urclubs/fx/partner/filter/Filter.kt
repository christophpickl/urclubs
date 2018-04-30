package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.filter.script.FilterScriptField
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

interface FilterTrigger {
    fun filter()
}

interface FilterSpec {
    val isIrrelevant: Boolean
    fun register(trigger: FilterTrigger)
    fun addToPredicates(predicates: MutableList<FilterPredicate>)
}


abstract class FilterScriptFieldSpec(
    private val field: FilterScriptField
) : FilterSpec {

    private val predicate get() = field.predicateProperty.get()

    override val isIrrelevant: Boolean get() = predicate == Filter.anyPredicate

    override fun register(trigger: FilterTrigger) {
        field.predicateProperty.addListener { _ ->
            trigger.filter()
        }
    }

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        if (predicate != null && predicate != Filter.anyPredicate) {
            predicates += predicate
        }
    }
}

