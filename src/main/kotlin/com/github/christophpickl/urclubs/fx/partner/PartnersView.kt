package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerImageSize
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.ImageId
import com.github.christophpickl.urclubs.fx.Images
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerSelectedEvent
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.geometry.Pos
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.reflect.KProperty1

val Rating.color: Color?
    get() = when (this) {
        Rating.SUPERB -> Color.GREEN
        Rating.GOOD -> Color.GREENYELLOW
        Rating.OK -> Color.ORANGE
        Rating.BAD -> Color.RED
        Rating.UNKNOWN -> null
    }

val Partner.favouriteImage: Image get() = if (favourited) Images[ImageId.FAVOURITE_FULL] else Images[ImageId.FAVOURITE_OUTLINE]
val Partner.wishlistedImage: Image get() = if (wishlisted) Images[ImageId.WISHLIST_FULL] else Images[ImageId.WISHLIST_OUTLINE]

class PartnersView : View() {

    private val logg = LOG {}
    private val partnersFilter: FilterPartnersView by inject()
    private val currentPartner: CurrentPartnerFx by inject()
    private val imagePadding = 5

    val table = tableview<Partner> {
        addClass(Styles.partnersTable)
        column("Picture", Partner::picture).apply {
            cellFormat {
                graphic = imageview(rowItem.picture.fxImageLil)
            }
            fixedWidth(PartnerImageSize.LITTLE.dimension.width)
        }

        column("Name", Partner::name).minWidth(200.0).maxWidth(400.0)

        imageColumn(Partner::favourited) { it.favouriteImage }
        imageColumn(Partner::wishlisted) { it.wishlistedImage }

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
        }.fixedWidth(60)
        column("Visits", Partner::totalVisits).fixedWidth(48)
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
            if (UrclubsConfiguration.Development.COLOR_MODE) backgroundColor += Color.AQUA
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

    private inline fun <reified S, T> TableView<S>.imageColumn(imageProperty: KProperty1<S, T>, crossinline imageExtractor: (S) -> Image): TableColumn<S, T> =
        column("", imageProperty).apply {
            fixedWidth(Images.size.width + imagePadding * 2)
            cellFormat {
                graphic = imageview { image = imageExtractor(rowItem) }
                alignment = Pos.CENTER
            }
        }

    private fun firePartnerSelected(partner: Partner) {
        logg.trace { "firePartnerSelected($partner)" }
        currentPartner.initPartner(partner)
        fire(PartnerSelectedEvent(partner))
    }

}
