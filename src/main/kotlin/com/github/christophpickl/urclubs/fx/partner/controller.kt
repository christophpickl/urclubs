package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import javafx.collections.FXCollections
import tornadofx.*

class PartnersFxController : Controller() {

    private val partnerService: PartnerService by di()

    val partners = FXCollections.observableArrayList<Partner>()!!

    init {
        subscribe<PartnerListRequest> {
            val partners = partnerService.readAll()
            fire(PartnerListEvent(partners))
        }
    }

}
