@file:Suppress("SimplifyBooleanWithConstants")

package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.DatabaseStartupType
import java.io.File

object UrclubsConfiguration {

    private val log = LOG {}

    val WINDOW_GAP = 10.0
    val PARTNER_WINDOW_WIDTH = 500.0

    val IS_PRODUCTION = (System.getProperty(SystemProperties.KEY_PRODUCTION) != null).also {
        if (it) log.info { "Production mode (-D${SystemProperties.KEY_PRODUCTION}) is enabled." }
    }
    val IS_DEVELOPMENT = !IS_PRODUCTION

    val HOME_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEVELOPMENT) ".urclubs_dev" else ".urclubs").also { file ->
        log.info { "Urclubs directory located at: ${file.canonicalPath}" }
    }

    val DATABASE_DIRECTORY = File(HOME_DIRECTORY, "database")
    val SHOW_SQL = IS_DEVELOPMENT

    val DB_STARTUP = DatabaseStartupType.Main
//    val DB_STARTUP = DatabaseStartupType.PrintSchema

    val DEVELOPMENT_COLORS = (false && IS_DEVELOPMENT).also { if (it) log.info { "Using colors mode." } }

    val CACHE_DIRECTORY = File(HOME_DIRECTORY, "cache")

    val IS_MAC = (System.getProperty(SystemProperties.KEY_IS_MAC) != null).also { enabled ->
        if (enabled) log.info("Mac mode (-D${SystemProperties.KEY_IS_MAC}) is enabled.")
    }
    val IS_NOT_MAC = !IS_MAC

    val URCLUBS_LOGS_DIRECTORY = File(HOME_DIRECTORY, "logs")
    val IS_LOGS_DISABLED = (System.getProperty(SystemProperties.KEY_DIABLE_LOGS) != null).also {
        if (it) log.info { "Logs are disabled due to passed -D${SystemProperties.KEY_DIABLE_LOGS}" }
    }

    object Development {
        val STUBBED_SYNCER = (false && IS_DEVELOPMENT).also { it.logIfEnabled("Using stubbed syncer logic.") }
        val STUBBED_MYCLUBS = (false && IS_DEVELOPMENT).also { it.logIfEnabled("Using stubbed MyClubs API.") }
        val FAST_SYNC = (true && IS_DEVELOPMENT).also { it.logIfEnabled("Using fast sync mode.") }
        private fun Boolean.logIfEnabled(message: String) {
            if (this) {
                log.error { "==DEV==> $message" }
            }
        }
    }

}

object SystemProperties {
    const val KEY_EMAIL = "urclubs.email"
    const val KEY_PASSWORD = "urclubs.password"
    const val KEY_PRODUCTION = "urclubs.production"
    const val KEY_IS_MAC = "urclubs.isMacApp"
    const val KEY_DIABLE_LOGS = "urclubs.disableLogs"
}
