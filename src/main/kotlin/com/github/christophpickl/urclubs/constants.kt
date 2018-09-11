@file:Suppress("SimplifyBooleanWithConstants")

package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.persistence.DatabaseStartupType
import java.io.File

enum class Environment(
    val syspropValue: String
) {
    PROD("prod"),
    TEST("test"),
    DEV("dev");

    companion object {
        val defaultEnvironment = DEV
    }
}

object UrclubsConfiguration {

    private val log = LOG {}

    // ENVIRONMENT
    // -----------------------------------------------------------------------------------------------------------------

    var environment: Environment = Environment.defaultEnvironment
        set(value) {
            log.info { "Programmatically setting environment to: $value" }
            field = value
        }

    init {
        val envValue = System.getProperty(SystemProperties.KEY_ENVIRONMENT, null)
        val selectedEnv = Environment.values().firstOrNull { it.syspropValue == envValue }
        if (selectedEnv != null) {
            log.info { "Selected environment is set to: $selectedEnv" }
        } else {
            log.info { "No environment activated, defaulting to: ${Environment.defaultEnvironment}" }
        }
        environment = selectedEnv ?: Environment.defaultEnvironment
    }

    val IS_DEVELOPMENT get() = environment == Environment.DEV

    val IS_MAC = (System.getProperty(SystemProperties.KEY_IS_MAC) != null).also { enabled ->
        if (enabled) log.info("Mac mode (-D${SystemProperties.KEY_IS_MAC}) is enabled.")
    }
    val IS_NOT_MAC = !IS_MAC

    // DIRECTORIES
    // -----------------------------------------------------------------------------------------------------------------

    val HOME_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEVELOPMENT) ".urclubs_dev" else ".urclubs").also { file ->
        log.info { "Urclubs directory located at: ${file.canonicalPath}" }
    }
    val URCLUBS_LOGS_DIRECTORY = File(HOME_DIRECTORY, "logs")
    val DATABASE_DIRECTORY = File(HOME_DIRECTORY, "database")
    val CACHE_DIRECTORY = File(HOME_DIRECTORY, "cache")

    // DATABASE
    // -----------------------------------------------------------------------------------------------------------------

    val DB_STARTUP = DatabaseStartupType.Main
    //    val DB_STARTUP = DatabaseStartupType.PrintSchema
    val SHOW_SQL = IS_DEVELOPMENT && false

    // LOGS
    // -----------------------------------------------------------------------------------------------------------------

    val IS_LOGS_DISABLED = (System.getProperty(SystemProperties.KEY_DIABLE_LOGS) != null).also {
        if (it) log.info { "Logs are disabled due to passed -D${SystemProperties.KEY_DIABLE_LOGS}" }
    }

    // DEV
    // -----------------------------------------------------------------------------------------------------------------

    object Development {
        val STUBBED_SYNCER get() = (true && IS_DEVELOPMENT).also { it.logIfEnabled("Using stubbed syncer logic.") }
        val STUBBED_MYCLUBS get() = (true && IS_DEVELOPMENT).also { it.logIfEnabled("Using stubbed MyClubs API.") }
        val FAST_SYNC get() = (false && IS_DEVELOPMENT).also { it.logIfEnabled("Using fast sync mode.") }
        val COLOR_MODE get() = (false && IS_DEVELOPMENT).also { if (it) log.info { "Using colors mode." } }

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
    const val KEY_ENVIRONMENT = "urclubs.environment"
    const val KEY_IS_MAC = "urclubs.isMacApp"
    const val KEY_DIABLE_LOGS = "urclubs.disableLogs"
}
