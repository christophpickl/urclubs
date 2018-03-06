package com.github.christophpickl.urclubs.testInfra

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.PersistenceModule
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.transactional
import com.google.inject.AbstractModule
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Guice
import org.testng.annotations.Test
import javax.inject.Inject
import javax.persistence.EntityManager


class TestDbModule : AbstractModule() {
    override fun configure() {
        install(PersistenceModule("jdbc:hsqldb:mem:test_db"))
    }
}

@Test
@Guice(modules = [TestDbModule::class])
abstract class DatabaseTest {

    private val log = LOG {}

    @Inject
    protected lateinit var em: EntityManager

    @BeforeMethod
    fun clearDb() {
        log.debug { "clearDb()" }
        val delete = em.criteriaBuilder.createCriteriaDelete<PartnerDbo>(PartnerDbo::class.java)
        delete.from(PartnerDbo::class.java)
        em.transactional {
            createQuery(delete).executeUpdate()
//            flush()
        }
    }

}
