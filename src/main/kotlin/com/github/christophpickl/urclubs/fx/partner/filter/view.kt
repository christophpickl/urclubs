package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.fx.Styles
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
        paddingBottom = Styles.partnersTableVerticalPadding

        label(text = "Filter: ").addClass(Styles.fontWhiteMedium)
        add(nameField)

        label(text = " Category: ").addClass(Styles.fontWhiteMedium)
        add(category)
    }

}

class CategoryFilterCell : ListCell<CategoryFilter>() {
    override fun updateItem(item: CategoryFilter?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}
