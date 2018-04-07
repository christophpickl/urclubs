package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.*

class CategoryFilterSpec(private val view: FilterPartnersView) : FilterSpec {

    private val log = LOG {}
    private val categoryFilter get() = view.category.selectedItem!!

    override val isIrrelevant: Boolean get() = categoryFilter == CategoryFilter.AnyCategory

    override fun register(trigger: FilterTrigger) {

        view.category.selectionModel.selectedItemProperty().addListener { _ ->
            log.trace { "Category filter changed to: ${view.category.selectedItem}" }
            trigger.filter()
        }
    }

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        val filter = categoryFilter
        if (filter is CategoryFilter.EnumCategory) {
            predicates += CategoryFilterPredicate(filter)
        }
    }

}
sealed class CategoryFilter {

    abstract val label: String

    object AnyCategory : CategoryFilter() {
        override val label = "Any"
    }

    data class EnumCategory(
        val category: Category
    ) : CategoryFilter() {
        override val label = category.label
    }

    companion object {
        val all: List<CategoryFilter> = mutableListOf(AnyCategory) +
            Category.values().map {
                EnumCategory(it)
            }
    }

}

private data class CategoryFilterPredicate(private val categoryFilter: CategoryFilter.EnumCategory) : FilterPredicate {
    override fun test(t: Partner) =
        t.category == categoryFilter.category
}
