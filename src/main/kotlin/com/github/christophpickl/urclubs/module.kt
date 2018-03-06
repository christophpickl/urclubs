package com.github.christophpickl.urclubs

import com.github.christophpickl.urclubs.domain.partner.PartnerModule
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.persistence.P3Mod
import com.github.christophpickl.urclubs.service.ServiceModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class MainModule : AbstractModule() {
    override fun configure() {
        bind(EventBus::class.java).toInstance(EventBus())
        bind(AllEventCatcher::class.java).asEagerSingleton()

        install(P3Mod())
        install(PartnerModule())
        install(MyclubsModule())
        install(ServiceModule())
    }
}
