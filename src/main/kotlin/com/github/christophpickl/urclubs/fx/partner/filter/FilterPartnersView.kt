package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.demoLaunchJavaFx
import com.github.christophpickl.urclubs.fx.partner.filter.flags.FavouritedFilterButton
import com.github.christophpickl.urclubs.fx.partner.filter.flags.WishlistedFilterButton
import tornadofx.*

class FilterPartnersView : View() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            demoLaunchJavaFx {
                vbox {
                    add(FilterPartnersView().root)
                }
            }
        }
    }

    val nameField = textfield {
        promptText = "Enter Name"
    }

    val category = combobox(values = CategoryFilter.all) {
        buttonCell = CategoryFilterCell()
        setCellFactory {
            CategoryFilterCell()
        }
        selectionModel.select(CategoryFilter.AnyCategory)
    }

    val visits = FilterScriptField().apply {
        promptText = "Any"
    }

    val favouritedFilterButton = FavouritedFilterButton()
    val wishlistedFilterButton = WishlistedFilterButton()

    override val root = flowpane {
        paddingBottom = Styles.partnersTableVerticalPadding

        label(text = "Filter: ").addClass(Styles.fontWhiteMedium)
        add(nameField)

        label(text = " Category: ").addClass(Styles.fontWhiteMedium)
        add(category)

        label(text = " Visits: ").addClass(Styles.fontWhiteMedium)
        add(visits)

//        label(text = " CredsLeft: ").addClass(Styles.fontWhiteMedium)
//        label(text = " Rating: ").addClass(Styles.fontWhiteMedium)

        add(favouritedFilterButton)
        add(wishlistedFilterButton)
    }

}
