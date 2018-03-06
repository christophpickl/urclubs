package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.QuitEvent
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.persist.PersistService
import org.hsqldb.jdbc.JDBCDataSource
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory


class DbInitializer @Inject constructor(
    service: PersistService,
    @DatabaseUrl private val databaseUrl: String
) {
    private val log = LOG {}

    init {
        log.debug { "Starting persist unit." }
        flywayMigration()
        service.start()
    }

    private fun flywayMigration() {
        log.debug { "flywayMigration() ... establishing new data source connection" }
        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        FlywayManager(dataSource).migrate()
        dataSource.connection.close()
    }
}

class DbExitializer @Inject constructor(
    bus: EventBus,
    private val em: EntityManager,
    private val emf: EntityManagerFactory
) {

    private val log = LOG {}
    init {
        bus.register(this)
    }

    @Subscribe
    fun onQuitEvent(@Suppress("UNUSED_PARAMETER") event: QuitEvent) {
        log.debug { "onQuitEvent(event) ... shutting down database" }
        em.close()
        emf.close()
    }
}
