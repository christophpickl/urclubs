package com.github.christophpickl.urclubs.fx.partners.detail

import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.FXEvent

class PartnerSelectedEvent(val partner: Partner) : FXEvent()

object PartnerSaveEvent : FXEvent()

class PartnerUpdatedFXEvent(val partner: Partner) : FXEvent()