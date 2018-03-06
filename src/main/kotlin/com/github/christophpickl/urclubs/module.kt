package com.github.christophpickl.urclubs

import com.github.christophpickl.urclubs.domain.partner.PartnerModule
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.service.ServiceModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class MainModule : AbstractModule() {
    override fun configure() {
        bind(EventBus::class.java).toInstance(EventBus())
        bind(AllEventCatcher::class.java).asEagerSingleton()

        install(PersistenceModule())
        install(PartnerModule())
        install(MyclubsModule())
        install(ServiceModule())
    }

//    @Provides
//    @Singleton
//    fun provideFoo() = Foo()

}
