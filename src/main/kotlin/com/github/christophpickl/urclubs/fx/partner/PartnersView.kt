package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.ImageSize
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerSelectedEvent
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
        column("Picture", Partner::picture).apply {
            cellFormat {
                graphic = imageview(rowItem.picture.fxImageLil)
            }
            fixedWidth(ImageSize.LITTLE.dimension.width)
        }

        column("Name", Partner::name).minWidth(200.0).maxWidth(400.0)
        column("Category") { features: TableColumn.CellDataFeatures<Partner, String> ->
            ReadOnlyStringWrapper(features.value.category.label)
        }.fixedWidth(100.0)
        column("Rating", Partner::rating).cellFormat {
            graphic = hbox {
                label(rowItem.rating.label)
                style {
                    backgroundColor += if (rowItem.rating.color != null) rowItem.rating.color!! else Color.GRAY
                }
            }
        }
        column("Credits") { features: TableColumn.CellDataFeatures<Partner, String> ->
            val partner = features.value
            ReadOnlyStringWrapper("${partner.creditsLeftThisPeriod}/${partner.maxCredits}")
        }.fixedWidth(40)
        column("Note", Partner::note)

        columnResizePolicy = SmartResize.POLICY
        contextmenu {
            item(name = "Ignore") {
                action { fire(IgnorePartnerFXEvent(selectedItem!!)) }
            }
        }
    }

    override val root = borderpane {
        style {
            if (UrclubsConfiguration.DEVELOPMENT_COLORS) backgroundColor += Color.AQUA
        }
        top {
            add(partnersFilter)
        }
        center {
            add(table)
        }
    }

    init {
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
