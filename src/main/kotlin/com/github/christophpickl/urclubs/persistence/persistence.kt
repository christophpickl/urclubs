package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.QuitEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.Provider
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

// could enhance DB classes: http://www.objectdb.com/java/jpa/tool/enhancer
// Enhancer.enhance("com.github.christophpickl.urclubs.persistence.*")

class PersistenceModule : AbstractModule() {
    override fun configure() {
        bind(EntityManager::class.java).toProvider(EntityManagerManager::class.java)
        bind(PartnerDao::class.java).to(PartnerObjectDbDao::class.java).asEagerSingleton()
    }
}

class EntityManagerManager @Inject constructor(
        bus: EventBus
) : Provider<EntityManager> {

    init {
        // MINOR register all beans automatically (?)
        bus.register(this)
    }

    private val log = LOG {}
    private val dbPath = "build/prod_db.odb"
    private var emFactory: EntityManagerFactory? = null
    private var em: EntityManager? = null

    override fun get(): EntityManager {
        em?.let { return it }

        log.debug { "Creating new EntityManager instance." }
        emFactory = Persistence.createEntityManagerFactory(dbPath)
        em = emFactory!!.createEntityManager()
        return em!!
    }

    @Subscribe
    fun onQuitEvent(event: QuitEvent) {
        log.debug { "onQuitEvent(event) ... shutting down database" }
        em?.close()
        emFactory?.close()
    }

}

fun EntityManager.transactional(action: EntityManager.() -> Unit) {
    transaction.begin()
    action(this)
    transaction.commit()
}
