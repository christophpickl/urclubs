package com.github.christophpickl.urclubs.persistence

import java.sql.Timestamp
import java.time.LocalDateTime
import javax.persistence.EntityManager

const val COL_LENGTH_LIL = 128
const val COL_LENGTH_MED = 512
const val COL_LENGTH_BIG = 5120
const val ONE_MB = 1024 * 1024

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

fun <T> EntityManager.persistTransactional(entity: T): T {
    transactional {
        persist(entity)
    }
    return entity
}

fun <T> EntityManager.transactional(action: EntityManager.() -> T): T {
    transaction.begin()
    var committed = false
    try {
        val result = action(this)
        transaction.commit()
        committed = true
        return result
    } finally {
        if (!committed) {
            transaction.rollback()
        }
    }
}

fun <T> EntityManager.persistAndReturn(entity: T) = entity.also { persist(entity) }

inline fun <reified T> EntityManager.queryList(query: String): List<T> {
    return createQuery(query, T::class.java).resultList
}

inline fun <reified T : Any> EntityManager.createCriteriaDeleteAll() =
    criteriaBuilder.createCriteriaDelete<T>(T::class.java).apply {
        from(T::class.java)
    }

inline fun <reified T : Any> EntityManager.deleteAll() {
    val delete = createCriteriaDeleteAll<T>()
    createQuery(delete).executeUpdate()
}

fun LocalDateTime.toTimestamp() = Timestamp.valueOf(this)
