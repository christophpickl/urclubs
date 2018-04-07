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
