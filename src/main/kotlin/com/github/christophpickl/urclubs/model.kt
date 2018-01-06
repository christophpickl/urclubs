package com.github.christophpickl.urclubs

import com.fasterxml.jackson.annotation.JsonProperty

data class User(
        @JsonProperty("user_id")
        val id: String,
        @JsonProperty("email")
        val email: String,
        @JsonProperty("firstname")
        val firstName: String,
        @JsonProperty("lastname")
        val lastName: String
)
