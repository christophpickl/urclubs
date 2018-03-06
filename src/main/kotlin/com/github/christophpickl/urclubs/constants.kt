package com.github.christophpickl.urclubs

import java.io.File

val URCLUBS_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEVELOPMENT) ".urclubs_dev" else ".urclubs")
val URCLUBS_DATABASE_DIRECTORY = File(URCLUBS_DIRECTORY, "database")
val IS_MAC = System.getProperty("urclubs.isMacApp") != null
