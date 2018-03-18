package com.github.christophpickl.urclubs

import com.github.christophpickl.urclubs.domain.activity.ActivityModule
import com.github.christophpickl.urclubs.domain.partner.PartnerModule
import com.github.christophpickl.urclubs.fx.FxViewModule
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.service.ServiceModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class MainModule : AbstractModule() {
    override fun configure() {
        bind(EventBus::class.java).toInstance(EventBus())
        bind(AllEventCatcher::class.java).asEagerSingleton()
        bind(QuitManager::class.java).asEagerSingleton()

        install(PersistenceModule())
        install(PartnerModule())
        install(ActivityModule())
        install(MyclubsModule())
        install(ServiceModule())
        install(FxViewModule())
    }

//    @Provides
//    @Singleton
//    fun provideFoo() = Foo()

}
