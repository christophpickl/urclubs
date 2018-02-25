package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.PartnersView
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.input.KeyCode
import tornadofx.*

class FilterPartnersController : Controller() {

    private val logg = LOG {}
    private val view: FilterPartnersView by inject()
    private val partnersView: PartnersView by inject()
    private var originalPartners: ObservableList<Partner>? = null

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
        if (originalPartners == null) {
            // TODO needs to be reset on PartnerListEvent
            originalPartners = partnersView.table.items
        }
        val filterName = view.nameField.text
        val categoryFilter = view.category.selectedItem!!

        if (filterName.isEmpty() &&
            categoryFilter == CategoryFilter.AnyCategory) {
            logg.debug { "Resetting filter." }
            partnersView.table.items = originalPartners
            return
        }

        val filteredItems = FilteredList<Partner>(originalPartners)
        filteredItems.setPredicate {
            if (filterName.isEmpty()) {
                true
            } else {
                it.name.contains(filterName, ignoreCase = true)
            }
                &&
                when (categoryFilter) {
                    CategoryFilter.AnyCategory -> true
                    is CategoryFilter.EnumCategory -> it.category == categoryFilter.category
                }

        }
        partnersView.table.items = filteredItems
    }


}
