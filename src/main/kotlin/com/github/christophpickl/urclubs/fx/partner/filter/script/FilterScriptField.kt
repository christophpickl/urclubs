package com.github.christophpickl.urclubs.fx.partner.filter.script

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.PartnerIntExtractor
import com.github.christophpickl.urclubs.fx.Styles
import com.github.christophpickl.urclubs.fx.demoLaunchJavaFx
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.onEscape
import javafx.beans.property.SimpleObjectProperty
import tornadofx.*


class FilterScriptField(val extractor: PartnerIntExtractor) : javafx.scene.control.TextField() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            demoLaunchJavaFx {
                FilterScriptField({ 0 }).apply {
                    predicateProperty.addListener { _ ->
                        println("change: ${predicateProperty.get()}")
                    }
                }
            }
        }
    }

    private val log = LOG {}
    val predicateProperty = SimpleObjectProperty<FilterPredicate>()
    private val parser = IntScriptParser(extractor)

    init {
        style {
            width = 20.0
        }
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
