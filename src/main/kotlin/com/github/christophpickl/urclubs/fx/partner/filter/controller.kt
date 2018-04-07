package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import tornadofx.*

class FilterPartnersController : Controller(), FilterTrigger {

    private val logg = LOG {}
    private val view: FilterPartnersView by inject()
    private val filters = listOf(
        NameFilterSpec(view),
        CategoryFilterSpec(view),
        VisitsFilterSpec(view)
        // ... add more here ...
    )

    init {
        filters.forEach { filter ->
            filter.register(this)
        }
    }

    override fun filter() {
        if (filters.all { it.isIrrelevant }) {
            logg.debug { "Resetting filter as all filters are set to irrelevant." }
            fire(ApplyFilterFXEvent.noFilter())
            return
        }

        val predicates = mutableListOf<FilterPredicate>()
        filters.forEach { it.addToPredicates(predicates) }
        fire(ApplyFilterFXEvent(Filter.SomeFilter(predicates)))
    }

}
