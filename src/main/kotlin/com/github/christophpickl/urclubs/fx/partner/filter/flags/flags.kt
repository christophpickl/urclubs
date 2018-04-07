package com.github.christophpickl.urclubs.fx.partner.filter.flags

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.Trilean
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.fx.partner.filter.FilterPredicate
import com.github.christophpickl.urclubs.fx.partner.filter.FilterSpec
import com.github.christophpickl.urclubs.fx.partner.filter.FilterTrigger
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.input.MouseButton
import tornadofx.*


abstract class FlagFilterSpec(
    private val button: FlagFilterButton,
    private val partnerFlagExtractor: Partner.() -> Boolean
) : FilterSpec {

    private val log = LOG {}
    private val filterState get() = button.state.get()

    override val isIrrelevant: Boolean get() = filterState == Trilean.None

    override fun register(trigger: FilterTrigger) {
        button.state.addListener { _ ->
            log.trace { "Some flag filter changed to: $filterState" }
            trigger.filter()
        }
    }

    override fun addToPredicates(predicates: MutableList<FilterPredicate>) {
        if (filterState != Trilean.None) {
            predicates += object : FilterPredicate {
                override fun test(partner: Partner) =
                    filterState.matches(partner.partnerFlagExtractor())
            }
        }
    }
}

abstract class FlagFilterButton(
    val imageTrue: Image,
    val imageFalse: Image
) : Button() {

    val state = SimpleObjectProperty<Trilean>()

    init {
        state.addListener { _ ->
            val newState = state.get()
            graphic = imageview {
                image = newState.image
                if (newState == Trilean.None) {
                    effectRemoveColors()
                }
            }
        }
        action {
            state.set(state.get().next())
        }
        setOnMouseReleased { e ->
            if (e.button == MouseButton.SECONDARY) {
                state.set(Trilean.None)
            }
        }
        state.set(Trilean.None) // initial set
    }

    private fun javafx.scene.Node.effectRemoveColors() {
        effect = javafx.scene.effect.ColorAdjust().apply { saturation = -1.0 }
    }

    private val Trilean.image
        get() = when (this) {
            Trilean.None, Trilean.False -> imageFalse
            Trilean.True -> imageTrue
        }
}
