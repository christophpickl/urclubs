package com.github.christophpickl.urclubs.testInfra

import com.github.christophpickl.urclubs.backend.FilterMycJson

fun FilterMycJson.Companion.testInstance() = FilterMycJson(
        date = emptyList(),
        time = emptyList(),
        type = emptyList()
)
