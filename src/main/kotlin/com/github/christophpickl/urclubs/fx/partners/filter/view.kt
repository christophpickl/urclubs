package com.github.christophpickl.urclubs.fx.partners.filter

import com.github.christophpickl.urclubs.domain.partner.Category
import javafx.scene.control.ListCell
import tornadofx.*

class CategoryFilterCell : ListCell<CategoryFilter>() {
    override fun updateItem(item: CategoryFilter?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}

class FilterPartnersView : View() {

    val nameField = textfield()
    val category = combobox(values = CategoryFilter.all) {
        buttonCell = CategoryFilterCell()
        setCellFactory {
            CategoryFilterCell()
        }
        selectionModel.select(CategoryFilter.AnyCategory)
    }

    override val root = flowpane {
        label(text = "Filter: ")
        add(nameField)
        add(category)
    }

}

sealed class CategoryFilter {

    abstract val label: String

    object AnyCategory : CategoryFilter() {
        override val label = "Any"
    }

    class EnumCategory(
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
