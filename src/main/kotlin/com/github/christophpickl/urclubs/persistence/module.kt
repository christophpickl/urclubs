package com.github.christophpickl.urclubs.persistence

import com.google.inject.AbstractModule
import javax.persistence.EntityManager

class PersistenceModule : AbstractModule() {
    override fun configure() {
        bind(EntityManager::class.java).toProvider(EntityManagerManager::class.java)
        bind(PartnerDao::class.java).to(PartnerObjectDbDao::class.java).asEagerSingleton()
    }
}
