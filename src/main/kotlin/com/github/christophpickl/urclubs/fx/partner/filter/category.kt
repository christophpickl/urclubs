package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner

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

data class CategoryFilterPredicate(private val categoryFilter: CategoryFilter.EnumCategory) : FilterPredicate {
    override fun test(t: Partner) =
        t.category == categoryFilter.category
}
