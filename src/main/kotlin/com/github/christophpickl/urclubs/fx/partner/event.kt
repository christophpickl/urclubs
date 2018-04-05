package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.*

object PartnerListRequestFXEvent : FXEvent(EventBus.RunOn.BackgroundThread)

class PartnerListFXEvent(val partners: List<Partner>) : FXEvent()

class IgnorePartnerFXEvent(val partner: Partner) : FXEvent()

class AddArtificialFinishedActivityFXEvent(val partner: Partner) : FXEvent()
