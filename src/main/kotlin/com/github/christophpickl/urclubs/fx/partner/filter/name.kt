package com.github.christophpickl.urclubs.fx.partner.filter

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import javafx.scene.input.KeyCode

class NameFilterSpec(private val view: FilterPartnersView) : FilterSpec {

    private val log = LOG {}
    private val input get() = view.nameField.text

    override val isIrrelevant: Boolean get() = input.isEmpty()

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        if (input.isNotEmpty()) {
            predicates += NameFilterPredicate(input)
        }
    }

    override fun register(trigger: FilterTrigger) {
        view.nameField.setOnKeyPressed { e ->
            if (e.code == KeyCode.ESCAPE) {
                log.trace { "Escape hit, resetting name filter." }
                view.nameField.text = ""
            }
        }

        view.nameField.textProperty().addListener { _ ->
            log.trace { "Name filter changed to: '${view.nameField.text}'" }
            trigger.filter()
        }
    }

}

private data class NameFilterPredicate(private val filterName: String) : FilterPredicate {
    override fun test(t: Partner) =
        t.name.contains(filterName, ignoreCase = true)
}
