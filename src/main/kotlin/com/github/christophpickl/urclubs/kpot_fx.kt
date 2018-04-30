package com.github.christophpickl.urclubs

import javafx.scene.input.KeyCode
import javafx.stage.Screen
import javafx.stage.Stage

fun Stage.fillPrimaryScreen() {
    val bounds = Screen.getPrimary().visualBounds
    x = bounds.minX
    y = bounds.minY
    width = bounds.width
    height = bounds.height
}

inline fun javafx.scene.Node.onEscape(crossinline func: () -> Unit) {
    setOnKeyPressed { e ->
        if (e.code == KeyCode.ESCAPE) {
            func()
        }
    }
}
