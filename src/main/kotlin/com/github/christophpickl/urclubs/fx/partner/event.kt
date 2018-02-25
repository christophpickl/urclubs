package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.EventBus
import tornadofx.FXEvent

object PartnerListRequest : FXEvent(EventBus.RunOn.BackgroundThread)

class PartnerListEvent(val partners: List<Partner>) : FXEvent()
