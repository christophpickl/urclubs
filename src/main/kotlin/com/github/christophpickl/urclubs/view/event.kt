package com.github.christophpickl.urclubs.view

import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.*

object PartnerListRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class PartnerListEvent(val partners: List<Partner>) : FXEvent()
