package com.github.christophpickl.urclubs

enum class Trilean {
    None,
    False,
    True;

    fun next(): Trilean = when (this) {
        Trilean.None -> False
        Trilean.False -> True
        Trilean.True -> None
    }

    fun matches(bool: Boolean) = when (this) {
        Trilean.None -> true
        Trilean.False -> !bool
        Trilean.True -> bool
    }
}
