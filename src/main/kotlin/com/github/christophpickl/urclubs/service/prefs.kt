package com.github.christophpickl.urclubs.service

import java.util.prefs.Preferences
import javax.inject.Inject

class PrefsManager @Inject constructor(
        quitManager: QuitManager
) : QuitListener {

    private val allPrefs = mutableListOf<Preferences>()

    init {
        quitManager.addQuitListener(this)
    }

    fun newPrefs(targetClass: Class<Any>) = Preferences.userRoot().node(targetClass.name)!!.also { pref ->
        allPrefs += pref
    }

    override fun onQuit() {
        allPrefs.forEach {
            it.flush()
        }
    }

}
