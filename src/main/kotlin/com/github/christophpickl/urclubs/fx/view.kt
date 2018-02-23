package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.fx.partners.PartnerListRequest
import com.github.christophpickl.urclubs.fx.partners.PartnersView
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncer
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.center
import tornadofx.hbox
import tornadofx.label
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

    private val syncer: UpcomingActivitySyncer by di()

    override val root = hbox {
        button("Foobar").setOnAction {
            syncer.sync()
        }
        button("Resync Partners").action {
            fire(SyncRequest)
        }
        button("Reload from DB").action {
            fire(PartnerListRequest)
        }
    }

}
