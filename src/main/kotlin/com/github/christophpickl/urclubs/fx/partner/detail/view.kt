package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.fx.CategoryCell
import tornadofx.*


class PartnerView : View() {

    private val logg = LOG {}
    val nameField = textfield()
    val category = combobox(values = Category.values().toList()) {
        buttonCell = CategoryCell()
        setCellFactory {
            CategoryCell()
        }
    }

    override val root = borderpane {
        center {
            form {
                fieldset("General Info") {
                    field("Name") {
                        add(nameField)
                    }
                    field("Category") {
                        add(category)
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
