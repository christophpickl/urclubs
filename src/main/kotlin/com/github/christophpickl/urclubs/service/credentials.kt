package com.github.christophpickl.urclubs.service

import com.google.inject.Provider

data class Credentials(
        val email: String,
        val password: String
)

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

    companion object {
        private const val KEY_EMAIL = "urclubs.email"
        private const val KEY_PASSWORD = "urclubs.password"
    }

    override fun get(): Credentials {
        val email = System.getProperty(KEY_EMAIL)
        val password = System.getProperty(KEY_PASSWORD)
        val notSetValues = listOfNotNull(email.orElse(KEY_EMAIL), password.orElse(KEY_PASSWORD))
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
