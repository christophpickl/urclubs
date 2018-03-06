package com.github.christophpickl.urclubs.fx

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        // Define our styles
        val devBlue by cssclass()
//        val bob by cssclass()
//        val alice by cssclass()

        // Define our colors
        val colorBlue = c("#5464a9")
    }

    init {
        devBlue {
            backgroundColor += colorBlue
        }
//        label {
//            fontSize = 20.px
//            fontWeight = FontWeight.BOLD
//            backgroundColor += c("#cecece")
//        }
    }
}
