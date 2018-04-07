package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.demoLaunchJavaFx
import com.github.christophpickl.urclubs.onEscape
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ListCell
import tornadofx.*

fun main(args: Array<String>) {
    demoLaunchJavaFx {
        FilterScriptField().apply {
            predicateProperty.addListener { _ ->
                println("change: ${predicateProperty.get()}")
            }
        }
    }
}

class FilterScriptField() : javafx.scene.control.TextField() {

    private val log = LOG {}
    val predicateProperty = SimpleObjectProperty<VisitFilterPredicate>()

    init {
        onEscape {
            log.trace { "Escape hit, resetting visits filter." }
            if (text != "") {
                text = ""
            }
        }
        textProperty().addListener { _ ->
            val predicate = VisitsInputParser.parse(text)
            if (predicate == null) {
                style {
                    backgroundColor += Styles.redDark
                }
            } else {
                predicateProperty.set(predicate)
                style {
                    backgroundColor += Styles.greyDark
                }
            }
        }
    }
}

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

    val visits = FilterScriptField()

    override val root = flowpane {
        paddingBottom = Styles.partnersTableVerticalPadding

        label(text = "Filter: ").addClass(Styles.fontWhiteMedium)
        add(nameField)

        label(text = " Category: ").addClass(Styles.fontWhiteMedium)
        add(category)

        label(text = " Visits: ").addClass(Styles.fontWhiteMedium)
        add(visits)

        label(text = " CredsLeft: ").addClass(Styles.fontWhiteMedium)
        label(text = " Rating: ").addClass(Styles.fontWhiteMedium)
        label(text = " Favo: ").addClass(Styles.fontWhiteMedium)
        label(text = " Wish: ").addClass(Styles.fontWhiteMedium)
    }

}

class CategoryFilterCell : ListCell<CategoryFilter>() {
    override fun updateItem(item: CategoryFilter?, empty: Boolean) {
        super.updateItem(item, empty)
        text = item?.label
    }
}
