package com.github.christophpickl.urclubs.fx.partners

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import javafx.collections.FXCollections
import tornadofx.Controller

class PartnersFxController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()

    val partners = FXCollections.observableArrayList<Partner>()!!

    init {
        subscribe<PartnerListRequest> {
            val partners = partnerService.readAll()
            fire(PartnerListEvent(partners))
        }
    }
//    fun reloadPartners() {
//        logg.debug { "reloadPartners()" }
//        runAsync {
//            listPartners()
//        } ui {
//            partners.setAll(it)
//        }
//    }


}
