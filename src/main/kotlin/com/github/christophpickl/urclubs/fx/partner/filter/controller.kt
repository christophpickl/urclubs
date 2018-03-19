package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import javafx.scene.input.KeyCode
import tornadofx.*
import java.util.function.Predicate

class FilterPartnersController : Controller() {

    private val logg = LOG {}
    private val view: FilterPartnersView by inject()

    init {
        view.nameField.setOnKeyPressed { e ->
            if (e.code == KeyCode.ESCAPE) {
                logg.trace { "Escape hit, resetting name filter." }
                view.nameField.text = ""
            }
        }
        view.nameField.textProperty().addListener { _ ->
            logg.trace { "Name filter changed to: '${view.nameField.text}'" }
            filter()
        }
        view.category.selectionModel.selectedItemProperty().addListener { _ ->
            logg.trace { "Category filter changed to: ${view.category.selectedItem}" }
            filter()
        }
    }

    private fun filter() {
        val filterName = view.nameField.text
        val categoryFilter = view.category.selectedItem!!

        if (filterName.isEmpty() &&
            categoryFilter == CategoryFilter.AnyCategory) {
            logg.debug { "Resetting filter." }
            fire(ApplyFilterFXEvent.noFilter())
            return
        }

        val predicates = mutableListOf<FilterPredicate>()
        if (filterName.isNotEmpty()) {
            predicates += NameFilterPredicate(filterName)
        }
        if (categoryFilter is CategoryFilter.EnumCategory) {
            predicates += CategoryFilterPredicate(categoryFilter)
        }
        fire(ApplyFilterFXEvent(Filter.SomeFilter(predicates)))
    }
}

sealed class Filter {
    object NoFilter : Filter() {
        val all = Predicate<Partner> { true }
    }

    data class SomeFilter(private val predicates: List<FilterPredicate>) : Filter() {
        init {
            assert(predicates.isNotEmpty(), { "At least one predicate must be set, otherwise use NoFilter instead." })
        }

        fun concatPredicates() = Predicate<Partner> { partner ->
            predicates.all { predicate ->
                predicate.test(partner)
            }
        }

    }
}

interface FilterPredicate : Predicate<Partner>

private data class NameFilterPredicate(private val filterName: String) : FilterPredicate {
    override fun test(t: Partner) =
        t.name.contains(filterName, ignoreCase = true)
}

private data class CategoryFilterPredicate(private val categoryFilter: CategoryFilter.EnumCategory) : FilterPredicate {
    override fun test(t: Partner) =
        t.category == categoryFilter.category
}
