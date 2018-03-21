package com.github.christophpickl.urclubs.fx

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        // Define our styles
        val myClass by cssclass()
//        val bob by cssclass()
//        val alice by cssclass()

        // Define our colors
        val orange = c("#F2412E")
        val white = c("#FFFFFF")
        val background = c("#FFFFFF")
        /*
        @font-face {
          font-family: "pilcrow";
          font-style: normal;
          font-weight: 700;
          src: url("/assets/web/fonts/PilcrowSoft-Bold.eot?#iefix") format("embedded-opentype"), url("/assets/web/fonts/PilcrowSoft-Bold.woff") format("woff"), url("/assets/web/fonts/PilcrowSoft-Bold.ttf") format("truetype"), url("/assets/web/fonts/PilcrowSoft-Bold.svg#PilcrowSoft-Bold") format("svg");
        }
         */
    }

    init {
        myClass {
            backgroundColor += orange
        }

        label {
//            fontSize = 20.px
//            fontWeight = FontWeight.BOLD
            backgroundColor += orange
        }
    }
}
