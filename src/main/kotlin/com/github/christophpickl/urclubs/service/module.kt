package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.google.inject.AbstractModule

class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(PartnerSyncer::class.java)
    }
}
