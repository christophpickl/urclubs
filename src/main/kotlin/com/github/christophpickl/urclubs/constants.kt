package com.github.christophpickl.urclubs

import java.io.File

val SYSPROP_IS_DEV = "urclubs.dev"
val IS_DEV_MODE = System.getProperty(SYSPROP_IS_DEV) != null

val URCLUBS_DIRECTORY = File(System.getProperty("user.home"), if (IS_DEV_MODE) ".urclubs_dev" else ".urclubs")

val URCLUBS_DATABASE_DIRECTORY = File(URCLUBS_DIRECTORY, "database")
