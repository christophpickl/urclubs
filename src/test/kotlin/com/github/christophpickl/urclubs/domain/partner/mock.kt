package com.github.christophpickl.urclubs.domain.partner

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify

fun PartnerService.captureUpdate(): Partner {
    val captor = argumentCaptor<Partner>()
    verify(this).update(captor.capture())
    return captor.firstValue
}
