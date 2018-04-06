package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerImageSize
import com.github.christophpickl.urclubs.fx.Images
import com.github.christophpickl.urclubs.fx.Styles
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.geometry.Pos
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.image.Image
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.reflect.KProperty1


class PartnersTable : TableView<Partner>() {

    private val imagePadding = 5

    init {
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

        column("Visits", Partner::totalVisits).fixedWidth(52)

        column("Last V.", Partner::lastVisitInDays).apply {
            fixedWidth(100.0)
        }.cellFormat {
            graphic = hbox {
                label(rowItem.lastVisitInDaysFormatted)
            }
        }

        column("Address") { features: TableColumn.CellDataFeatures<Partner, String> ->
            ReadOnlyStringWrapper(features.value.addresses.firstOrNull() ?: "")
        }

        column("Note", Partner::note)

        columnResizePolicy = SmartResize.POLICY
    }

    private val Partner.lastVisitInDaysFormatted
        get() = when (lastVisitInDays) {
            null -> "-"
            0 -> "Today"
            else -> "$lastVisitInDays Days"
        }

    private inline fun <reified S, T> TableView<S>.imageColumn(imageProperty: KProperty1<S, T>, crossinline imageExtractor: (S) -> Image): TableColumn<S, T> =
        column("", imageProperty).apply {
            fixedWidth(Images.size.width + imagePadding * 2)
            cellFormat {
                graphic = imageview { image = imageExtractor(rowItem) }
                alignment = Pos.CENTER
            }
        }

}
