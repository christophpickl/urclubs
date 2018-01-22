package com.github.christophpickl.urclubs.testInfra

import com.fasterxml.jackson.databind.node.ArrayNode
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestResult
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import java.io.File
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

fun ArrayNode.textValues() = elements().asSequence().map { it.textValue() }.toList()

open class TestListenerAdapter : ITestListener {
    override fun onStart(context: ITestContext) {}
    override fun onTestStart(result: ITestResult) {}
    override fun onTestSkipped(result: ITestResult) {}
    override fun onTestSuccess(result: ITestResult) {}
    override fun onTestFailure(result: ITestResult) {}
    override fun onTestFailedButWithinSuccessPercentage(result: ITestResult) {}
    override fun onFinish(context: ITestContext) {}
}

abstract class DatabaseTest {

    private val dbPath = "build/test_db.odb"
    private lateinit var emFactory: EntityManagerFactory
    protected lateinit var em: EntityManager

    @BeforeMethod
    fun setupDb() {
        emFactory = Persistence.createEntityManagerFactory(dbPath)
        em = emFactory.createEntityManager()
    }

    @AfterMethod
    fun tearDownDb() {
        em.close()
        emFactory.close()
        File(dbPath).delete()
    }

}
