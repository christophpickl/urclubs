@file:Suppress("SimplifyBooleanWithConstants")

package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG

private val log = LOG {}

const val SYSPROP_DEVELOPMENT = "urclubs.development"

val IS_DEVELOPMENT = (System.getProperty(SYSPROP_DEVELOPMENT) != null).also {
    log.info { "Development mode (-D$SYSPROP_DEVELOPMENT=1) is ${if (it) "enabled" else "disabled"}." }
}

val DEVELOPMENT_FAST_SYNC = (IS_DEVELOPMENT && false).also {
    if (it) log.info { "Using fast sync mode." }
}
