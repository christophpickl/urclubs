package com.github.christophpickl.urclubs

import com.fasterxml.jackson.annotation.JsonValue


data class Credentials(
        val email: String,
        val password: String
)
