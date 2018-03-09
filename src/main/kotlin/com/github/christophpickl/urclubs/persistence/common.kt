package com.github.christophpickl.urclubs.persistence

import javax.persistence.EntityManager

// could enhance DB classes: http://www.objectdb.com/java/jpa/tool/enhancer
// Enhancer.enhance("com.github.christophpickl.urclubs.persistence.*")

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

fun EntityManager.transactional(action: EntityManager.() -> Unit) {
    transaction.begin()
    action(this)
    transaction.commit()
}

inline fun <reified T: Any> EntityManager.createCriteriaDeleteAll() = criteriaBuilder.createCriteriaDelete<T>(T::class.java).apply {
    from(T::class.java)
}
inline fun <reified T: Any> EntityManager.deleteAll() {
    val delete = createCriteriaDeleteAll<T>()
    transactional {
        createQuery(delete).executeUpdate()
    }
}
