@file:Suppress("SimplifyBooleanWithConstants")

package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.DatabaseStartupType
import java.io.File


private val log = LOG {}

val IS_PRODUCTION = (System.getProperty(SystemProperties.KEY_PRODUCTION) != null).also {
    log.info { "Production mode (-D${SystemProperties.KEY_PRODUCTION}=1) is ${if (it) "enabled" else "disabled"}." }
}
val IS_DEVELOPMENT = !IS_PRODUCTION

val DEVELOPMENT_FAST_SYNC = (false && IS_DEVELOPMENT).also { if (it) log.info { "Using fast sync mode." } }
val DEVELOPMENT_COLORS = (false && IS_DEVELOPMENT).also { if (it) log.info { "Using colors mode." } }

object UrclubsConfiguration {
    const val SHOW_SQL = true

    val DB_STARTUP = DatabaseStartupType.Main
//    val DB_STARTUP = DatabaseStartupType.PrintSchema

}

val URCLUBS_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEVELOPMENT) ".urclubs_dev" else ".urclubs").also { file ->
    log.info { "Urclubs directory located at: ${file.canonicalPath}" }
}
val URCLUBS_DATABASE_DIRECTORY = File(URCLUBS_DIRECTORY, "database")
val IS_MAC = (System.getProperty(SystemProperties.KEY_IS_MAC) != null).also { enabled ->
    log.info("Mac mode is ${if (enabled) "enabled" else "disabled"}")
}

val IS_LOGS_DISABLED = (System.getProperty(SystemProperties.KEY_DIABLE_LOGS) != null).also {
    if (it) log.info { "Logs are disabled due to passed -D${SystemProperties.KEY_DIABLE_LOGS}" }
}

// -Durclubs.email=xxx -Durclubs.password=xxx
object SystemProperties {
    const val KEY_EMAIL = "urclubs.email"
    const val KEY_PASSWORD = "urclubs.password"
    const val KEY_PRODUCTION = "urclubs.production"
    const val KEY_IS_MAC = "urclubs.isMacApp"
    const val KEY_DIABLE_LOGS = "urclubs.disableLogs"
}
