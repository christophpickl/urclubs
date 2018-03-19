package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Category

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
