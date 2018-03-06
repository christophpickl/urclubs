package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.QuitEvent
import com.google.common.eventbus.Subscribe
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.FlywayException
import org.flywaydb.core.api.MigrationInfo
import org.flywaydb.core.api.callback.BaseFlywayCallback
import org.hsqldb.DatabaseManager
import org.hsqldb.HsqlException
import org.hsqldb.error.ErrorCode
import java.sql.Connection
import java.sql.SQLException
import javax.sql.DataSource

class FlywayManager(
    private val ds: DataSource
) : DatabaseManager() {

    private val log = LOG {}
    private val migrationLocation = "/urclubs/migrations"

    init {
        Runtime.getRuntime().addShutdownHook(Thread(Runnable {
            log.debug("Database shutdown hook is running and dancing around.")
            closeConnection()
        }, "DatabaseShutdownHookThread"))
    }

    private var databaseConnected: Boolean = false

    fun migrateDatabase() {
        log.info("migrateDatabase()")

        val flyway = buildFlyway()
        try {
            flyway.migrate()
        } catch (e: FlywayException) {
            if (e.message?.contains("validate failed", true) == true) {
                log.warn("Migration failed due to validation error, going to repair the database first and try migrating then.", e)
                flyway.repair()
                log.info("DB repair done, going to migrate now again.")
                flyway.migrate()
            } else {
                val dbLockException = DatabaseLockedException.buildByCause(e)
                throw dbLockException ?: e
            }
        }
        databaseConnected = true

        log.debug("Good luck, DB migration was successfull.")
    }

    private fun buildFlyway() = Flyway().apply {
        setLocations(migrationLocation)
        dataSource = ds
        val myCallback = object : BaseFlywayCallback() {
            override fun beforeEachMigrate(connection: Connection, info: MigrationInfo) {
                log.debug("Execute migration step v{}: {}", info.version, info.description)
            }
        }
        setCallbacks(*callbacks.toMutableList().apply { add(myCallback) }.toTypedArray())
    }

    @Subscribe
    fun onQuit(event: QuitEvent) {
        closeConnection()
    }

    private fun closeConnection() {
        if (!databaseConnected) {
            log.warn("Not going to close database connection as it was never successfully opened")
            return
        }
        log.debug("Closing database connection.")
        try {
            ds.connection.close()
        } catch (e: Exception) {
            // when there is a lock, the shutdown hook will fail, avoid this!
            log.error("Could not close database connection.", e)
        }
    }

}

class DatabaseLockedException(message: String, cause: FlywayException) : Exception(message, cause) {
    companion object {
        fun buildByCause(e: FlywayException): DatabaseLockedException? {
            val hsqlCause = parseCause(e) ?: return null
            return if (hsqlCause.isCausedByLockFailure()) DatabaseLockedException("Database locked: ${hsqlCause.message}", e) else null
        }

        private fun parseCause(e: FlywayException): HsqlException? {
            val sqlCause = (e.cause ?: return null) as? SQLException ?: return null
            return (sqlCause.cause ?: return null) as? HsqlException ?: return null
        }

        private fun HsqlException.isCausedByLockFailure() =
        // strangely returns "-451" ?
            Math.abs(errorCode) == ErrorCode.LOCK_FILE_ACQUISITION_FAILURE
    }

}
