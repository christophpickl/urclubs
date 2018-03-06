package com.github.christophpickl.urclubs.persistence

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.QuitEvent
import com.github.christophpickl.urclubs.URCLUBS_DATABASE_DIRECTORY
import com.github.christophpickl.urclubs.configureLogging
import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.google.inject.AbstractModule
import com.google.inject.BindingAnnotation
import com.google.inject.Guice
import com.google.inject.Provides
import com.google.inject.Singleton
import com.google.inject.persist.PersistService
import com.google.inject.persist.jpa.JpaPersistModule
import org.hsqldb.jdbc.JDBCDataSource
import java.io.File
import java.util.Properties
import javax.inject.Inject
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence
import javax.sql.DataSource


val URCLUBS_PERSISTENCE_UNIT = "urclubs.punit"

fun main(args: Array<String>) {
    configureLogging()
    val inMem = "jdbc:hsqldb:mem:mymemdb;hsqldb.tx=mvcc"
    Guice.createInjector(P3Mod(inMem))

//    Guice.createInjector(P2Mod(inMem))
//    Guice.createInjector(PersistenceModule("jdbc:hsqldb:mem:mymemdb;hsqldb.tx=mvcc"))
//    System.setProperty(SYSPROP_DEVELOPMENT, "1")
//    Guice.createInjector(PersistenceModule())

}

class DbInitializer @Inject constructor(
    service: PersistService,
    @DatabaseUrl private val databaseUrl: String
) {
    private val log = LOG {}

    init {
        log.debug { "Starting persist unit." }

        migrateDb()

        service.start()
    }


    private fun migrateDb() {
        log.debug { "migrateDb()" }
        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        FlywayManager(dataSource).migrateDatabase()
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

// jdbc:hsqldb:file:/Users/wu/.urclubs_dev/database/database
@Retention
@Target(AnnotationTarget.VALUE_PARAMETER)
@BindingAnnotation
annotation class DatabaseUrl

class P3Mod(definedDatabaseUrl: String? = null) : AbstractModule() {

    private val log = LOG {}
    private val defaultDbUrl: String = "jdbc:hsqldb:file:${File(URCLUBS_DATABASE_DIRECTORY, "database").absolutePath}"
    private val databaseUrl = definedDatabaseUrl ?: defaultDbUrl

    private val persistenceProperties = Properties().apply {
        put("javax.persistence.jdbc.url", databaseUrl)
        put("hibernate.show_sql", true)

        // https://stackoverflow.com/questions/438146/hibernate-hbm2ddl-auto-possible-values-and-what-they-do
//        put("hibernate.hbm2ddl.auto", "create") // in order to get hibernate printing out its own SQL to create DDL (dont forget to disable flyway!)
        put("hibernate.hbm2ddl.auto", "validate")
    }

    override fun configure() {
        log.info { "Configuring JPA with URL: '$databaseUrl'" }
        install(JpaPersistModule(URCLUBS_PERSISTENCE_UNIT).properties(persistenceProperties))
        bind(DbInitializer::class.java).asEagerSingleton()
        bind(DbExitializer::class.java).asEagerSingleton()
        bindConstant().annotatedWith(DatabaseUrl::class.java).to(databaseUrl)
    }

}


/**
 * @param databaseUrl e.g.: "jdbc:hsqldb:mem:testdb", or "jdbc:hsqldb:file:/foo/db"
 */
class P2Mod(definedDatabaseUrl: String? = null) : AbstractModule() {
    companion object {
        private val ENTITY_MANAGER_CACHE = ThreadLocal<EntityManager>()
    }

    private val DEFAULT_DB_URL: String = "jdbc:hsqldb:file:${File(URCLUBS_DATABASE_DIRECTORY, "database").absolutePath}"

    private val databaseUrl = definedDatabaseUrl ?: DEFAULT_DB_URL
    private val log = LOG {}

    override fun configure() {
        log.debug("configure() ... using database URL; '{}'", databaseUrl)
        initFlyway()
    }

    private fun initFlyway() {
//        bind(DataSource::class.java).toInstance(dataSource)
        val em = provideEntityManager(provideEntityManagerFactory())
        println(em.javaClass.name)
//        val dataSource =
//        val flyway = FlywayManager(dataSource)
//        flyway.migrateDatabase()
    }

    private val persistenceProperties = Properties().apply {
        put("javax.persistence.jdbc.url", databaseUrl)
        put("hibernate.show_sql", true)
    }

    @Provides
    @Singleton
    fun provideEntityManagerFactory() = Persistence.createEntityManagerFactory(URCLUBS_PERSISTENCE_UNIT, persistenceProperties)

    @Provides
    fun provideEntityManager(entityManagerFactory: EntityManagerFactory) =
        ENTITY_MANAGER_CACHE.get() ?: entityManagerFactory.createEntityManager().also {
            ENTITY_MANAGER_CACHE.set(it)
        }
}

// MINOR maybe use guice's JpaPersistModule? => https://github.com/google/guice/wiki/JPA

class PersistenceModule(private val databaseUrl: String? = null) : AbstractModule() {
    private val log = LOG {}
    private val DEFAULT_DB_URL: String = "jdbc:hsqldb:file:${File(URCLUBS_DATABASE_DIRECTORY, "database").absolutePath}"
    //            DEFAULT_DB_URL = "jdbc:hsqldb:mem:mymemdb;hsqldb.tx=mvcc"
    private val DB_USER = "SA"

    override fun configure() {
//        bind(EntityManager::class.java).toProvider(EntityManagerManager::class.java).`in`(Scopes.SINGLETON)

        val databaseUrl = databaseUrl ?: DEFAULT_DB_URL
        log.debug("configure() ... using database URL; '{}'", databaseUrl)

        val dataSource = JDBCDataSource()
        dataSource.url = databaseUrl
        dataSource.user = DB_USER

        bind(DataSource::class.java).toInstance(dataSource)
        val flyway = FlywayManager(dataSource)
        bind(FlywayManager::class.java).toInstance(flyway)
        flyway.migrateDatabase()

    }
}
