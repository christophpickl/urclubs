package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.demoLaunchJavaFx
import com.github.christophpickl.urclubs.fx.partner.filter.flags.FavouritedFilterButton
import com.github.christophpickl.urclubs.fx.partner.filter.flags.WishlistedFilterButton
import com.github.christophpickl.urclubs.fx.partner.filter.script.FilterScriptField
import com.github.christophpickl.urclubs.fx.partner.filter.script.IntScriptParser
import com.github.christophpickl.urclubs.fx.partner.filter.script.IntScriptParserConfig
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

    val visits = FilterScriptField(IntScriptParser({ totalVisits })).apply {
        promptText = "Any"
    }

    val rating = FilterScriptField(IntScriptParser({ rating.intValue }, IntScriptParserConfig.empty.copy(
        minValue = Rating.minIntValue,
        maxValue = Rating.maxIntValue
    ))).apply {
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

        label(text = " Rating: ").addClass(Styles.fontWhiteMedium)
        add(rating)

        label(text = "  ")
        add(favouritedFilterButton)
        label(text = " ")
        add(wishlistedFilterButton)
    }

}
