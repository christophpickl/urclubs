package com.github.christophpickl.urclubs.persistence

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import javax.persistence.EntityManager

class PersistenceModule : AbstractModule() {
    override fun configure() {
        bind(EntityManager::class.java).toProvider(EntityManagerManager::class.java).`in`(Scopes.SINGLETON)
    }
}
