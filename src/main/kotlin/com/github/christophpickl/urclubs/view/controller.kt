package com.github.christophpickl.urclubs.view

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import javafx.collections.FXCollections
import tornadofx.*

class PartnersController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()

    val partners = FXCollections.observableArrayList<Partner>()!!

    init {
        subscribe<PartnerListRequest> {
            val partners = listPartners()
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

    private fun listPartners(): List<Partner> {
        logg.debug { "listPartners()" }
        return partnerService.readAll()
    }


}
