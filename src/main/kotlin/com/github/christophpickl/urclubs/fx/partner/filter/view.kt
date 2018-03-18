package com.github.christophpickl.urclubs.fx.partner.filter

import javafx.scene.control.ListCell
import tornadofx.*

class FilterPartnersView : View() {

    val nameField = textfield() {
        promptText = "Enter Name"
    }
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

class CategoryFilterCell : ListCell<CategoryFilter>() {
    override fun updateItem(item: CategoryFilter?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}
