package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.CategoryCell
import com.github.christophpickl.urclubs.fx.OpenWebsiteFXEvent
import com.github.christophpickl.urclubs.fx.RatingCell
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.util.converter.NumberStringConverter
import tornadofx.*

class PartnerDetailView : View() {

    private val logg = LOG {}
    private val currentPartner: CurrentPartnerFx by inject()
    private val widthOfFormContainingName = 350.0

    val addressesBox = VBox()

    override val root = gridpane {
        addClass(Styles.partnerDetailPanel)
        style {
            paddingTop = 20
        }

        row {
            vbox(spacing = 10.0) {
                gridpaneConstraints {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.NEVER
                    vAlignment = VPos.TOP
                }
                alignment = Pos.TOP_LEFT
                imageview().apply {
                    addClass(Styles.showHandOnHover)
                    setOnMouseClicked {
                        logg.debug { "Clicked on picture." }
                        fire(ChoosePictureFXEvent(this@PartnerDetailView))
                    }
                }.imageProperty().bindBidirectional(currentPartner.picture)

                alignment = Pos.BOTTOM_LEFT
                button("Save") {
                    action {
                        logg.trace { "Save button clicked." }
                        fire(RequestPartnerSaveFXEvent)
                    }
                    shortcut("Meta+S")
                }
            }
            form {
                prefWidth = widthOfFormContainingName
                minWidth = widthOfFormContainingName
                gridpaneConstraints {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.NEVER
                    vAlignment = VPos.TOP
                }
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    field("Name") {
                        textfield() {
                            textProperty().bindBidirectional(currentPartner.name)
                            style {
                                fontSize = 18.px
                            }
                        }
                    }

                    field("Category") {
                        combobox(
                            property = currentPartner.category,
                            values = Category.Ordered.allOrdered.observable()
                        ) {
                            buttonCell = CategoryCell()
                            setCellFactory {
                                CategoryCell()
                            }
                        }
                    }
                    field("Rating") {
                        combobox(
                            property = currentPartner.rating,
                            values = Rating.Ordered.allOrdered.observable()
                        ) {
                            buttonCell = RatingCell()
                            setCellFactory {
                                RatingCell()
                            }
                        }
                    }

                    field("Flags") {
                        hbox {
                            spacing = 10.0
                            checkbox(text = "Favourite") {
                                bind(currentPartner.favourited)
                            }
                            checkbox(text = "Wishlist") {
                                bind(currentPartner.wishlisted)
                            }
                        }
                    }
                }
            }

            form {
                useMaxWidth = true
                gridpaneConstraints {
                    vhGrow = Priority.ALWAYS
                    vAlignment = VPos.TOP
                }
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    field("Tags") {
                        label { bind(currentPartner.tags) }
                    }
                    field("Address") {
                        add(addressesBox)
                    }
                    field("Visits") {
                        hbox {
                            alignment = Pos.BASELINE_LEFT
                            label(currentPartner.creditsLeftThisPeriod)

                            label(" of ")
                            textfield().apply {
                                prefWidth = 40.0
                                textProperty().bindBidirectional(currentPartner.maxCredits, NumberStringConverter())
                            }

                            label(" Left. Total: ")
                            label(currentPartner.totalVisits)
                        }
                    }
                    field("Links") {
                        vbox {
                            hyperlink {
                                textProperty().bind(currentPartner.linkMyclubs)
                                tooltip { textProperty().bind(currentPartner.linkMyclubs) }
                                enableWhen { currentPartner.linkMyclubs.isNotEmpty }
                                setOnAction { fire(OpenWebsiteFXEvent(url = currentPartner.original.linkMyclubs)) }
                            }

                            hyperlink {
                                textProperty().bind(currentPartner.linkPartner)
                                tooltip { textProperty().bind(currentPartner.linkPartner) }
                                enableWhen { currentPartner.linkPartner.isNotNull }
                                setOnAction { fire(OpenWebsiteFXEvent(url = currentPartner.original.linkPartner ?: "")) }
                            }
                        }
                    }
                }
            }

            form {
                gridpaneConstraints {
                    vgrow = Priority.ALWAYS
                    hgrow = Priority.NEVER
                    vAlignment = VPos.TOP
                }
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    field {
                        textarea {
                            textProperty().bindBidirectional(currentPartner.note)
                        }
                    }
                }
            }
        }
    }
}
