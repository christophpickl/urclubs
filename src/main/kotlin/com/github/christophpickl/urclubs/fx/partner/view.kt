package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.kpotpourri.common.logging.LOG
import tornadofx.View
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.box
import tornadofx.button
import tornadofx.center
import tornadofx.field
import tornadofx.fieldset
import tornadofx.form
import tornadofx.hbox
import tornadofx.px
import tornadofx.style
import tornadofx.textfield

class PartnerView : View() {

    private val logg = LOG {}
    val nameField = textfield()

    override val root = borderpane {
        center {
            form {
                fieldset("General Info") {
                    field("Name") {
                        add(nameField)
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
