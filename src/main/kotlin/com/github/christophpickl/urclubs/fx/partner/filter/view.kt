package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.fx.Styles
import javafx.application.Application
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.ListCell
import tornadofx.*


fun main(args: Array<String>) {
    class DemoView : View() {
        override val root = vbox {
            add(FilterScriptField().apply {
                predicateProperty.addListener { _ ->
                    println("change: ${predicateProperty.get()}")
                }
            })
        }
    }

    class DemoApp : App(
        primaryView = DemoView::class,
        stylesheet = Styles::class
    )
    Application.launch(DemoApp::class.java, *args)
}

class FilterScriptField() : javafx.scene.control.TextField() {

    val predicateProperty = SimpleObjectProperty<VisitFilterPredicate>()

    init {
        // TODO on press ESC set to 'any'
        textProperty().addListener { _ ->
            val predicate = VisitsInputParser.parse(text)
            if (predicate == null) {
                style {
                    borderColor += box(Styles.red)
                }
            } else {
                predicateProperty.set(predicate)
                style {
                    borderColor += box(Styles.green)
                }
            }
        }
        text = VisitsInputParser.anyScript
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
