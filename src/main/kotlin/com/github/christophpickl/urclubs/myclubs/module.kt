package com.github.christophpickl.urclubs.myclubs

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class MyclubsModule : AbstractModule() {
    override fun configure() {
        bind(MyClubsApi::class.java).to(MyClubsHttpApi::class.java).`in`(Scopes.SINGLETON)
        bind(MyclubsUtil::class.java).asEagerSingleton()
    }
}
