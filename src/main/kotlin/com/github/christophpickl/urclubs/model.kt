package com.github.christophpickl.urclubs

data class Credentials(
        val email: String,
        val password: String
)

data class Partner(
        val idDbo: Long,
        val idMyc: String,
        val name: String
        // rating
        // note
)
