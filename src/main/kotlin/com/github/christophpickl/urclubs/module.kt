package com.github.christophpickl.urclubs

import com.github.christophpickl.urclubs.domain.activity.ActivityModule
import com.github.christophpickl.urclubs.domain.partner.PartnerModule
import com.github.christophpickl.urclubs.myclubs.MyclubsModule
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.service.AllEventsCatcher
import com.github.christophpickl.urclubs.service.Credentials
import com.github.christophpickl.urclubs.service.MetaInf
import com.github.christophpickl.urclubs.service.MetaInfProvider
import com.github.christophpickl.urclubs.service.PropertiesFileCredentialsProvider
import com.github.christophpickl.urclubs.service.QuitManager
import com.github.christophpickl.urclubs.service.ServiceModule
import com.google.common.eventbus.EventBus
import com.google.inject.AbstractModule

class MainModule : AbstractModule() {
    override fun configure() {
        bind(EventBus::class.java).toInstance(EventBus())
        bind(AllEventsCatcher::class.java).asEagerSingleton()
        bind(QuitManager::class.java).asEagerSingleton()
        bind(MetaInf::class.java).toInstance(MetaInfProvider.get())
        bind(Credentials::class.java).toProvider(PropertiesFileCredentialsProvider())

        install(PersistenceModule())
        install(PartnerModule())
        install(ActivityModule())
        install(MyclubsModule())
        install(ServiceModule())
    }

//    @Provides
//    @Singleton
//    fun provideFoo() = Foo()

}
