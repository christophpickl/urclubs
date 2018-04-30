package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.demoLaunchJavaFx
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.onEscape
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.TextField
import tornadofx.*

class FilterScriptField(
    parser: IntScriptParser
) : TextField() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            demoLaunchJavaFx {
                FilterScriptField(IntScriptParser({ 0 })).apply {
                    predicateProperty.addListener { _ ->
                        println("change: ${predicateProperty.get()}")
                    }
                }
            }
        }
    }

    val predicateProperty = SimpleObjectProperty<FilterPredicate>()

    private val log = LOG {}

    init {
        prefWidth = 80.0
        onEscape {
            log.trace { "Escape hit, resetting filter." }
            if (text != "") {
                text = ""
            }
        }
        textProperty().addListener { _ ->
            val predicate = parser.parse(text)
            if (predicate == null) {
                style {
                    backgroundColor += Styles.redDark
                }
            } else {
                predicateProperty.set(predicate)
                style {
                    backgroundColor += Styles.greyDark
                }
            }
        }
    }
}
