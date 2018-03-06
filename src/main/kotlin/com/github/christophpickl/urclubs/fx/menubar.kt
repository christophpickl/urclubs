package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequest
import javafx.scene.control.MenuBar
import tornadofx.*

class MenuBarController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()

    fun createDummyData() {
        logg.info { "createDummyData()" }
        Partner.dummies.forEach {
            partnerService.create(it)
        }
        fire(PartnerListRequest)
    }


}

class MyMenuBar(private val menuBarController: MenuBarController) : MenuBar() {

    init {
        menu("Develop") {
            item("Create Dummy Data").action {
                menuBarController.createDummyData()
            }
        }
    }
}
