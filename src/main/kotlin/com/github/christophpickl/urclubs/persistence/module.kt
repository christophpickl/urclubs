package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.URCLUBS_DATABASE_DIRECTORY
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.configureLogging
import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Guice
import com.google.inject.persist.jpa.JpaPersistModule
import java.io.File
import java.util.Properties

enum class DatabaseStartupType(
    val runMigration: Boolean,

    // https://stackoverflow.com/questions/438146/hibernate-hbm2ddl-auto-possible-values-and-what-they-do
    val hibernateAutoCommand: String

) {
    Main(
        runMigration = true,
        hibernateAutoCommand = "validate"
    ),
    PrintSchema(
        runMigration = false,
        hibernateAutoCommand = "create"
    )
    ;
}

/**
 * @param definedDatabaseUrl e.g.: "jdbc:hsqldb:mem:mymemdb", or "jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database"
 */
class PersistenceModule(definedDatabaseUrl: String? = null) : AbstractModule() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            configureLogging()
            Guice.createInjector(PersistenceModule("jdbc:hsqldb:mem:mymemdb"))
        }
    }

    private val log = LOG {}
    private val persistenceUnitName = "urclubs.punit"
    private val defaultDbUrl: String = "jdbc:hsqldb:file:${File(URCLUBS_DATABASE_DIRECTORY, "database").absolutePath}"
    private val databaseUrl = definedDatabaseUrl ?: defaultDbUrl

    init {
        log.debug { "definedDatabaseUrl: '$definedDatabaseUrl'" }
    }

    private val persistenceProperties = Properties().apply {
        put("javax.persistence.jdbc.url", databaseUrl)
        put("hibernate.show_sql", UrclubsConfiguration.SHOW_SQL)
        put("hibernate.hbm2ddl.auto", UrclubsConfiguration.DB_STARTUP.hibernateAutoCommand)
    }

    override fun configure() {
        log.info { "Configuring JPA with URL: '$databaseUrl'" }
        log.info { "Properties: $persistenceProperties" }
        install(JpaPersistModule(persistenceUnitName).properties(persistenceProperties))
        bind(DbInitializer::class.java).asEagerSingleton()
        bind(DbExitializer::class.java).asEagerSingleton()
        bindConstant().annotatedWith(DatabaseUrl::class.java).to(databaseUrl)
    }

}

@Retention
@Target(AnnotationTarget.VALUE_PARAMETER)
@BindingAnnotation
annotation class DatabaseUrl
