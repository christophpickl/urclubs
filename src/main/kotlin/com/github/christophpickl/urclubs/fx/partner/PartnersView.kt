package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.ImageId
import com.github.christophpickl.urclubs.fx.Images
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerDetailView
import com.github.christophpickl.urclubs.fx.partner.detail.PartnerSelectedFXEvent
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPartnersView
import javafx.scene.control.Label
import javafx.scene.image.Image
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

val Partner.favouriteImage: Image get() = if (favourited) Images[ImageId.FAVOURITE_FULL] else Images[ImageId.FAVOURITE_OUTLINE]
val Partner.wishlistedImage: Image get() = if (wishlisted) Images[ImageId.WISHLIST_FULL] else Images[ImageId.WISHLIST_OUTLINE]

class PartnersView : View() {

    private val logg = LOG {}
    private val partnersFilter: FilterPartnersView by inject()
    private val partnerDetailView: PartnerDetailView by inject()
    private val currentPartner: CurrentPartnerFx by inject()
    private lateinit var displayedPartnersLabel: Label

    val table = PartnersTable().apply {
        contextmenu {
            item(name = "Ignore Partner") {
                action { fire(IgnorePartnerFXEvent(selectedItem!!)) }
            }
            item(name = "Add Finished Activity") {
                action { fire(AddArtificialFinishedActivityFXEvent(selectedItem!!)) }
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
            borderpane {
                center { add(table) }
                bottom {
                    displayedPartnersLabel = label {
                        style {
                            textFill = Styles.white
                            fontSize = 8.pt
                        }
                    }
                }
            }
        }
        bottom {
            add(partnerDetailView)
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

    fun numberOfDisplayedPartners(number: Int) {
        displayedPartnersLabel.text = "Displaying $number partner${if (number == 1) "" else "s"}"
    }

    private fun firePartnerSelected(partner: Partner) {
        logg.trace { "firePartnerSelected($partner)" }
        currentPartner.initPartner(partner)
        fire(PartnerSelectedFXEvent(partner))
    }

}
