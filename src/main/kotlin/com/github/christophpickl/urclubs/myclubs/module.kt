package com.github.christophpickl.urclubs.myclubs

import com.google.inject.AbstractModule

class MyclubsModule : AbstractModule() {
    override fun configure() {
        bind(MyClubsApi::class.java).to(MyClubsHttpApi::class.java)
    }
}
