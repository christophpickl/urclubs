package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.service.QuitListener
import com.github.christophpickl.urclubs.service.QuitManager
import com.github.christophpickl.urclubs.UrclubsConfiguration
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
        if (UrclubsConfiguration.DB_STARTUP.runMigration) {
            flywayMigration()
        } else {
            log.warn { "Flyway database migration is disabled." }
        }
        log.debug { "Starting persist unit." }
        service.start()
    }

    private fun flywayMigration() {
        log.debug { "flywayMigration() ... establishing new data source connection: '$databaseUrl'" }
        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        FlywayManager(dataSource).migrate()
        dataSource.connection.close()
    }
}

class DbExitializer @Inject constructor(
    quitManager: QuitManager,
    private val em: EntityManager,
    private val emf: EntityManagerFactory
) : QuitListener {
    private val log = LOG {}

    init {
        quitManager.addQuitListener(this)
    }

    override fun onQuit() {
        log.debug { "onQuit() ... shutting down database" }
        em.close()
        emf.close()
    }

}
