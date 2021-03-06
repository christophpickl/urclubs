package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.service.sync.FinishedActivitySyncer
import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.github.christophpickl.urclubs.service.sync.UpcomingActivitySyncer
import com.google.inject.AbstractModule

class ServiceModule : AbstractModule() {
    override fun configure() {
        bind(PartnerSyncer::class.java)
        bind(FinishedActivitySyncer::class.java)
        bind(UpcomingActivitySyncer::class.java)
        bind(PrefsManager::class.java).asEagerSingleton()
        bind(Clock::class.java).to(RealClock::class.java).asEagerSingleton()
    }
}
