package com.github.christophpickl.urclubs.myclubs

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class MyclubsModule : AbstractModule() {
    override fun configure() {
        bind(Http::class.java).to(HttpImpl::class.java)
        bind(MyclubsUtil::class.java).asEagerSingleton()
        bind(MyClubsApi::class.java).annotatedWith(HttpApi::class.java).to(MyClubsHttpApi::class.java).`in`(Scopes.SINGLETON)

        bind(MyClubsCachedApi::class.java).`in`(Scopes.SINGLETON)
        bind(MyClubsApi::class.java).to(MyClubsCachedApi::class.java)
        bind(MyClubsCacheManager::class.java).to(MyClubsCachedApi::class.java)

    }
}
