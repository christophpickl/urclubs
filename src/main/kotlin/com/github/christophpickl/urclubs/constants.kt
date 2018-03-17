package com.github.christophpickl.urclubs

import com.github.christophpickl.urclubs.persistence.DatabaseStartupType
import java.io.File

object UrclubsConfiguration {
    const val SHOW_SQL = true

    val DB_STARTUP = DatabaseStartupType.Main
//    val DB_STARTUP = DatabaseStartupType.PrintSchema

}

val URCLUBS_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEVELOPMENT) ".urclubs_dev" else ".urclubs")
val URCLUBS_DATABASE_DIRECTORY = File(URCLUBS_DIRECTORY, "database")
val IS_MAC = System.getProperty(SystemProperties.KEY_IS_MAC) != null

// -Durclubs.email=xxx -Durclubs.password=xxx -Durclubs.development=1
object SystemProperties {
    const val KEY_EMAIL = "urclubs.email"
    const val KEY_PASSWORD = "urclubs.password"
    const val SYSPROP_DEVELOPMENT = "urclubs.development"
    const val KEY_IS_MAC = "urclubs.isMacApp"
}
