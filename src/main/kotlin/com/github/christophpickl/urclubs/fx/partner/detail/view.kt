package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.Rating
import com.github.christophpickl.urclubs.fx.CategoryCell
import com.github.christophpickl.urclubs.fx.OpenWebsiteFXEvent
import com.github.christophpickl.urclubs.fx.RatingCell
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import javafx.application.Application
import javafx.collections.FXCollections
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.stage.Stage
import javafx.util.converter.NumberStringConverter
import tornadofx.*

fun main(args: Array<String>) {
    class DummyApp : App(primaryView = PartnerView::class) {
        override fun start(stage: Stage) {
            super.start(stage)
            stage.centerOnScreen()
            find<CurrentPartnerFx>().initPartner(Partner.Dummies.superbEms)
        }
    }
    Application.launch(DummyApp::class.java, *args)
}

class ChoosePictureFXEvent(val requestor: View) : FXEvent()

class PartnerView : View() {

    companion object {
        const val WIDTH = 400.0
    }

    private val logg = LOG {}
    private val currentPartner: CurrentPartnerFx by inject()

    override val root = borderpane {
        top {
            vbox {
                alignment = Pos.CENTER
                imageview().apply {
                    setOnMouseClicked {
                        logg.debug { "Clicked on picture." }
                        fire(ChoosePictureFXEvent(this@PartnerView))
                    }
                }.imageProperty().bindBidirectional(currentPartner.picture)
            }
        }
        center {
            form {
                fieldset(labelPosition = Orientation.HORIZONTAL) {
                    field("Name") {
                        textfield().textProperty().bindBidirectional(currentPartner.name)
                    }
                    field("Short") {
                        label().textProperty().bind(currentPartner.shortName)
                    }
                    field("Category") {
                        combobox(
                            property = currentPartner.category,
                            values = FXCollections.observableArrayList(Category.Ordered.allOrdered)
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
                            values = FXCollections.observableArrayList(Rating.Ordered.allOrdered)
                        ) {
                            buttonCell = RatingCell()
                            setCellFactory {
                                RatingCell()
                            }
                        }
                    }
                    field("Note") {
                        textarea {
                            textProperty().bindBidirectional(currentPartner.note)
                        }
                    }
                    field("Flags") {
                        hbox {
                            checkbox(text = "Favourite") {
                                bind(currentPartner.favourited)
                            }
                            checkbox(text = "Wishlist") {
                                bind(currentPartner.wishlisted)
                            }
                        }
                    }
                    field("Credits") {
                        hbox {
                            label("Left: ")
                            label(currentPartner.creditsLeftThisPeriod)

                            label("  Max: ")
                            textfield().apply {
                                prefWidth = 40.0
                                textProperty().bindBidirectional(currentPartner.maxCredits, NumberStringConverter())
                            }

                            label("  Visits: ")
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
                                enableWhen { currentPartner.linkPartner.isNotEmpty }
                                setOnAction { fire(OpenWebsiteFXEvent(url = currentPartner.original.linkPartner)) }
                            }
                        }
                    }
                    field("foo") {
                        label("bar")
                    }
                }
            }
        }
        bottom {
            hbox {
                style {
                    padding = box(20.px)
                }
                button("Save").setOnAction {
                    logg.trace { "Save button clicked." }
                    fire(PartnerSaveEvent)
                }
            }
        }
    }

    init {
        title = "Partner"
    }
}
