package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.urclubs.IS_DEVELOPMENT
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequest
import com.github.christophpickl.urclubs.fx.partner.PartnersView
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncer
import tornadofx.*

class MainView : View() {

    private val bottomView: BottomView by inject()
    private val partnersView: PartnersView by inject()
    private val menuBarController: MenuBarController by inject()

    override val root = vbox {
        add(MyMenuBar(menuBarController))
        borderpane {
            top {
                label("hello urclubs")
            }
            center {
                add(partnersView)
            }
            bottom {
                add(bottomView)
            }
        }
    }

    init {
        title = "UrClubs" + if (IS_DEVELOPMENT) " - DEVELOPMENT" else ""
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
