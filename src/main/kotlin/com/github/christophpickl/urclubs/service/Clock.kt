package com.github.christophpickl.urclubs.service

import java.time.LocalDateTime

interface Clock {
    fun now(): LocalDateTime
}

class RealClock : Clock {
    override fun now() = LocalDateTime.now()!!
}
