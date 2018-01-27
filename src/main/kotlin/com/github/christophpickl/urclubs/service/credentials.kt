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
    override fun get() = Credentials(
        email = System.getProperty("urclubs.email")!!,
        password = System.getProperty("urclubs.password")!!
    )

}
