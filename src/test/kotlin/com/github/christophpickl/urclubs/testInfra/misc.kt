package com.github.christophpickl.urclubs.testInfra

import com.fasterxml.jackson.databind.node.ArrayNode
import com.github.christophpickl.urclubs.Environment
import com.github.christophpickl.urclubs.UrclubsConfiguration
import org.testng.ITestContext
import org.testng.ITestListener
import org.testng.ITestResult
import org.testng.annotations.BeforeSuite
import org.testng.annotations.Test

@Test
class SetupEnvironmentNonTest {
    @BeforeSuite
    fun `init environment to TEST`() {
        UrclubsConfiguration.environment = Environment.TEST
    }
}

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

