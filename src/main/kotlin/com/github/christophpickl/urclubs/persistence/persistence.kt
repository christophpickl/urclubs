package com.github.christophpickl.urclubs.persistence

import javax.persistence.EntityManager

// could enhance DB classes: http://www.objectdb.com/java/jpa/tool/enhancer
// Enhancer.enhance("com.github.christophpickl.urclubs.persistence.*")

fun EntityManager.transactional(action: EntityManager.() -> Unit) {
    transaction.begin()
    action(this)
    transaction.commit()
}
