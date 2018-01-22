package com.github.christophpickl.urclubs.persistence

// could enhance DB classes: http://www.objectdb.com/java/jpa/tool/enhancer
// Enhancer.enhance("com.github.christophpickl.urclubs.persistence.*")

interface HasId {
    val id: Long?
}

fun HasId.ensureNotPersisted() {
    if (id != null && id != 0L) {
        throw IllegalStateException("ID must be 0 for: $this")
    }
}

fun HasId.ensurePersisted() {
    if (id == null || id == 0L) {
        throw IllegalStateException("ID must be set for: $this")
    }
}
