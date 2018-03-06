package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.URCLUBS_DATABASE_DIRECTORY
import com.github.christophpickl.urclubs.configureLogging
import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Guice
import com.google.inject.persist.jpa.JpaPersistModule
import java.io.File
import java.util.Properties

/**
 * @param definedDatabaseUrl e.g.: "jdbc:hsqldb:mem:mymemdb", or "jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database"
 */
class PersistenceModule(definedDatabaseUrl: String? = null) : AbstractModule() {

    companion object {
        const val URCLUBS_PERSISTENCE_UNIT = "urclubs.punit"
        const val SHOW_SQL = false
        // https://stackoverflow.com/questions/438146/hibernate-hbm2ddl-auto-possible-values-and-what-they-do
        const val AUTO_COMMAND = "validate" // "create" ... in order to get hibernate printing out its own SQL to create DDL (dont forget to disable flyway!)

        @JvmStatic
        fun main(args: Array<String>) {
            configureLogging()
            Guice.createInjector(PersistenceModule("jdbc:hsqldb:mem:mymemdb"))
        }
    }

    private val log = LOG {}
    private val defaultDbUrl: String = "jdbc:hsqldb:file:${File(URCLUBS_DATABASE_DIRECTORY, "database").absolutePath}"
    private val databaseUrl = definedDatabaseUrl ?: defaultDbUrl

    private val persistenceProperties = Properties().apply {
        put("javax.persistence.jdbc.url", databaseUrl)
        put("hibernate.show_sql", SHOW_SQL)
        put("hibernate.hbm2ddl.auto", AUTO_COMMAND)
    }

    override fun configure() {
        log.info { "Configuring JPA with URL: '$databaseUrl'" }
        install(JpaPersistModule(URCLUBS_PERSISTENCE_UNIT).properties(persistenceProperties))
        bind(DbInitializer::class.java).asEagerSingleton()
        bind(DbExitializer::class.java).asEagerSingleton()
        bindConstant().annotatedWith(DatabaseUrl::class.java).to(databaseUrl)
    }

}

@Retention
@Target(AnnotationTarget.VALUE_PARAMETER)
@BindingAnnotation
annotation class DatabaseUrl
