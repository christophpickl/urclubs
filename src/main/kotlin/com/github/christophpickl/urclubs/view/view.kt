package com.github.christophpickl.urclubs.view

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.*

class MainView : View() {

    private val logg = LOG {}
    private val bottomView: BottomView by inject()

    override val root = borderpane {
        top {
            label("hello urclubs")
        }
        center {
            tableview<Partner> {
                column("Name", Partner::name)
                column("Rating", Partner::rating)
                column("Address", Partner::address)
                columnResizePolicy = SmartResize.POLICY
                subscribe<PartnerListEvent> { event ->
                    logg.trace { "Received PartnerListEvent, updating table items." }
                    items.setAll(event.partners)
                }
            }
        }
    }
    init {
        root.bottom = bottomView.root
        title = "UrClubs"
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
    }

    private fun increment() {
        counter.value += 1
    }
}
