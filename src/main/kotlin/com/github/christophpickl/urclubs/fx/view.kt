package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.fx.partners.PartnerListRequest
import com.github.christophpickl.urclubs.fx.partners.PartnersView
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.View
import tornadofx.action
import tornadofx.bind
import tornadofx.borderpane
import tornadofx.button
import tornadofx.center
import tornadofx.hbox
import tornadofx.label
import tornadofx.px
import tornadofx.style
import tornadofx.top

class MainView : View() {

    private val bottomView: BottomView by inject()
    private val partnersView: PartnersView by inject()

    override val root = borderpane {
        top {
            label("hello urclubs")
        }
        center {
            add(partnersView)
        }
    }

    init {
        root.bottom = bottomView.root
        title = "UrClubs"

        fire(PartnerListRequest)
    }
}

class BottomView : View() {

    private val counter = SimpleIntegerProperty()

    override val root = hbox {
        label {
            bind(counter)
            style { fontSize = 25.px }
        }
        button("Increment").setOnAction {
            increment()
        }
        button("reload").action {
            fire(PartnerListRequest)
        }
        button("resync").action {
            fire(SyncRequest)
        }
    }

    private fun increment() {
        counter.value += 1
    }
}
