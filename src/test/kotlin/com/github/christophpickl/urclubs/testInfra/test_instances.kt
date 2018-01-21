package com.github.christophpickl.urclubs.testInfra

import com.github.christophpickl.urclubs.backend.FilterJson

fun FilterJson.Companion.testInstance() = FilterJson(
        date = emptyList(),
        time = emptyList(),
        type = emptyList()
)
