package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.DEVELOPMENT_COLORS
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerSelectedEvent
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerUpdatedFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.scene.control.TableColumn
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import tornadofx.*

val Rating.color: Color?
    get() = when (this) {
        Rating.SUPERB -> Color.GREEN
        Rating.GOOD -> Color.GREENYELLOW
        Rating.OK -> Color.ORANGE
        Rating.BAD -> Color.RED
        Rating.UNKNOWN -> null
    }

class PartnersView : View() {

    private val logg = LOG {}

    // https://github.com/edvin/tornadofx/wiki/Utilities
    private val partnersFilter: FilterPartnersView by inject()
    private val currentPartner: CurrentPartnerFx by inject()

    val table = tableview<Partner> {
        column("Name", Partner::name).minWidth(200.0).maxWidth(400.0)
        column("Category") { features: TableColumn.CellDataFeatures<Partner, String> ->
            ReadOnlyStringWrapper(features.value.category.label)
        }.fixedWidth(100.0)
        column("Rating") { features: TableColumn.CellDataFeatures<Partner, String> ->
            ReadOnlyStringWrapper(features.value.rating.label)
        }
            .fixedWidth(100.0)
            .cellFormat {
                text = it
                style {
                    backgroundColor += if (rowItem.rating.color != null) rowItem.rating.color!! else Color.GRAY
                }
            }

        column("Address", Partner::address)
        columnResizePolicy = SmartResize.POLICY

        contextmenu {
            item(name = "Ignore") {
                action { fire(IgnorePartnerFXEvent(selectedItem!!)) }
            }
        }
    }

    override val root = borderpane {
        style {
            if (DEVELOPMENT_COLORS) backgroundColor += Color.AQUA
        }
        top {
            add(partnersFilter)
        }
        center {
            add(table)
        }
    }

    init {
        subscribe<PartnerListEvent> { event ->
            logg.trace { "Received PartnerListEvent (partners.size=${event.partners.size}), updating table items." }
            // TODO go through controller instead and apply filter
            table.items.setAll(event.partners)
        }
        subscribe<PartnerUpdatedFXEvent> { event ->
            val partner = event.partner
            logg.trace { "Updating partner in table: $partner" }
            val index = table.items.indexOfFirst { it.idDbo == partner.idDbo }
            if (index == -1) throw IllegalStateException("Could not find updated partner in table: $partner") // TODO when search (=filter) is active this will fail??
            table.items[index] = partner // FIXME when filtering, saving doesnt work... need to set on original list somehow...
            table.selectionModel.select(index)
        }
        table.onUserSelect(clickCount = 1) {
            firePartnerSelected(it)
        }
        var previousSelected: Partner? = null
        table.setOnKeyPressed {
            if (it.code == KeyCode.UP || it.code == KeyCode.DOWN) {
                table.selectedItem?.let {
                    previousSelected = it
                }
            }
        }
        table.onSelectionChange {
            table.selectedItem?.let {
                if (previousSelected != null && it != previousSelected) {
                    firePartnerSelected(it)
                    previousSelected = null
                }
            }
        }
    }

    private fun firePartnerSelected(partner: Partner) {
        logg.trace { "firePartnerSelected($partner)" }
        currentPartner.initPartner(partner)
        fire(PartnerSelectedEvent(partner))
    }

}
