package com.github.christophpickl.urclubs.service

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.SystemProperties
import com.github.christophpickl.urclubs.URCLUBS_DIRECTORY
import com.google.inject.Provider
import com.natpryce.konfig.Configuration
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.ConfigurationProperties.Companion.systemProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.Misconfiguration
import com.natpryce.konfig.PropertyGroup
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import java.io.File

fun main(args: Array<String>) {
    println(PropertiesFileCredentialsProvider().get())
}

data class Credentials(
    val email: String,
    val password: String
) {
    companion object
}

class CliArgsCredentialsProvider(private val args: Array<String>) : Provider<Credentials> {
    override fun get(): Credentials {
        if (args.size != 2) {
            throw Exception("Please provide credentials via CLI arguments!")
        }
        return Credentials(
            email = args[0],
            password = args[1]
        )
    }
}

class SystemPropertyCredentialsProvider : Provider<Credentials> {

    override fun get(): Credentials {
        val email = System.getProperty(SystemProperties.KEY_EMAIL)
        val password = System.getProperty(SystemProperties.KEY_PASSWORD)
        val notSetValues = listOfNotNull(email.orElse(SystemProperties.KEY_EMAIL), password.orElse(SystemProperties.KEY_PASSWORD))
        if (notSetValues.isNotEmpty()) {
            throw IllegalStateException("Expected to have set system properties: ${notSetValues.joinToString(", ")}")
        }
        return Credentials(
            email = email,
            password = password
        )
    }

    private fun String?.orElse(elseValue: String): String? {
        if (this != null) {
            return null
        }
        return elseValue
    }

}

class PropertiesFileCredentialsProvider : Provider<Credentials> {

    private val log = LOG {}

    @Suppress("ClassName")
    private object login : PropertyGroup() {
        val email by stringType
        val password by stringType
    }

    override fun get(): Credentials {
        val loginFile = File(URCLUBS_DIRECTORY, "login.properties")
        log.debug { "Loading credentials from file: ${loginFile.canonicalPath}" }
        if (!loginFile.exists()) {
            throw CredentialsLoadException("Required file doesnt exist at: ${loginFile.canonicalPath}")
        }
        val config = systemProperties() overriding
            EnvironmentVariables() overriding
            ConfigurationProperties.fromFile(loginFile)
        val email = config.safeGet(login.email)
        val password = config.safeGet(login.password)
        return Credentials(
            email = email,
            password = password
        )
    }

    private fun <T> Configuration.safeGet(key: Key<T>): T {
        try {
            return this.get(key)
        } catch (e: Misconfiguration) {
            throw CredentialsLoadException("Failed to load credentials for '${key.name}'!", e)
        }
    }
}

class CredentialsLoadException(message: String, cause: Exception? = null) : Exception(message, cause)
