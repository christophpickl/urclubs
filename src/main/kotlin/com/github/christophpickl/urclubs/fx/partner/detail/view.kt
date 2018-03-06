package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.fx.CategoryCell
import com.github.christophpickl.urclubs.fx.partner.CurrentPartnerFx
import javafx.collections.FXCollections
import tornadofx.*

class PartnerView : View() {

    companion object {
        val WIDTH = 300.0
    }
    private val logg = LOG {}
    private val currentPartner: CurrentPartnerFx by inject()

    override val root = borderpane {
        center {
            form {
                fieldset("General Info") {
                    field("Name") {
                        textfield().textProperty().bindBidirectional(currentPartner.name)
                    }
                    field("Category") {
                        combobox(
                            property = currentPartner.category,
                            values = FXCollections.observableArrayList(*Category.values())
                        ) {
                            buttonCell = CategoryCell()
                            setCellFactory {
                                CategoryCell()
                            }
                        }
                    }
                    field("Note") {
                        textarea {
                            textProperty().bindBidirectional(currentPartner.note)
                        }
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
        title = "Partner Details"
    }
}
