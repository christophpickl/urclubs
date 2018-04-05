package com.github.christophpickl.urclubs.fx.partner.detail

import com.github.christophpickl.urclubs.domain.partner.Partner
import tornadofx.*

class PartnerSelectedEvent(val partner: Partner) : FXEvent()

object PartnerSaveEvent : FXEvent()

class PartnerUpdatedFXEvent(val partner: Partner) : FXEvent()

class ChoosePictureFXEvent(val requestor: View) : FXEvent()
