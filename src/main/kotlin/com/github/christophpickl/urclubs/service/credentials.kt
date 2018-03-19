package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.SystemProperties
import com.google.inject.Provider

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
