package com.github.christophpickl.urclubs.fx

import javafx.application.Application
import tornadofx.*

private lateinit var _child: javafx.scene.layout.VBox.() -> javafx.scene.Node

class DemoView : View() {
    override val root = borderpane {
        center {
            vbox {
                add(_child())
            }
        }
    }
}

class DemoApp : App(
    primaryView = DemoView::class,
    stylesheet = Styles::class
)

fun demoLaunchJavaFx(child: javafx.scene.layout.VBox.() -> javafx.scene.Node) {
    _child = child
    Application.launch(DemoApp::class.java)
}
