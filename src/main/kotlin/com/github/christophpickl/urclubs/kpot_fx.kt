package com.github.christophpickl.urclubs

import javafx.stage.Screen
import javafx.stage.Stage

fun Stage.fillPrimaryScreen() {
    val bounds = Screen.getPrimary().visualBounds
    x = bounds.minX
    y = bounds.minY
    width = bounds.width
    height = bounds.height
}
