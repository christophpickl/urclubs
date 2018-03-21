package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.text.FontWeight
import tornadofx.*

// CREDO: hover change color over all interactive fields (buttons, input fields) + style orange

class Styles : Stylesheet() {

    private val log = LOG {}

    companion object {

        val partnersTableVerticalPadding = 10

        // SPECIFIC COMPONENTS
        val partnersTable by cssclass()
        val pictureChooser by cssclass()

        // GENERAL COMPONENTS
        val mainPanel by cssclass()
        val partnerPanel by cssclass()

        // FONTS
        val fontOrangeMedium by cssclass()
        val fontWhiteMedium by cssclass()

        // COLORS
        val orange = c("#F2412E")
        val orangeBright = c("#F28775")
        val white = c("#FFFFFF")
        val greyVeryBright = c("#3B3843")
        val greyBright = c("#292530")
        val greyDark = c("#231F2A")

        object Colors {
            val interactiveNormal = greyBright
            val interactiveHover = greyVeryBright
        }
    }

    init {

        val handOnHover = mixin {
            and(hover) {
                cursor = javafx.scene.Cursor.HAND
            }
        }

        root {
            backgroundColor += greyDark
            //  -fx-control-inner-background-alt: -fx-control-inner-background ; disable leftover empty table rows
        }

        mainPanel {
            padding = box(20.px)
        }

        partnerPanel {
            padding = box(20.px)
            label {
                textFill = white
            }
            field {
                textFill = white
            }
        }

        pictureChooser {
            +handOnHover
        }

        // FONTS
        // =============================================================================================================

        fontOrangeMedium {
            textFill = orange
        }

        fontWhiteMedium {
            textFill = white
        }

        s(fontOrangeMedium, fontWhiteMedium) {
            fontWeight = FontWeight.BOLD
            fontSize = 18.px
        }

        // GENERAL COMPONENTS
        // =============================================================================================================

        val flat = mixin {
            backgroundInsets += box(0.px)
            borderColor += box(javafx.scene.paint.Color.DARKGRAY)
        }

        val orangeBorder = mixin {
            borderColor += box(orange)
            borderStyle += BorderStrokeStyle.SOLID
        }

        val backgroundHoverColors = mixin {
            textFill = white
            backgroundColor += greyVeryBright
            and(hover) {
                backgroundColor += greyBright
            }
        }

        val theBigThree = mixin {
            +flat
            +orangeBorder
            +backgroundHoverColors
        }

        textArea {
            content {
                +flat
                +backgroundHoverColors
            }
        }

        hyperlink {
            textFill = orange
            underline = true
            and(hover) {
                textFill = orangeBright
            }
        }

        checkBox {
            textFill = white
            +handOnHover
            box {
                +theBigThree
            }
            and(hover) {
                textFill = orange
            }
            and(selected) {
                textFill = orange
                fontWeight = FontWeight.BOLD
                mark {
                    backgroundColor += white
                }
                box {
                }
            }
        }

        s(textInput, button, comboBox) {
            +theBigThree
        }

        textInput {
        }

        button {
            textFill = orange
            fontWeight = FontWeight.BOLD
        }

        comboBox {
            fontWeight = FontWeight.BOLD
            listCell {
                backgroundColor += greyVeryBright
                textFill = white
                fontWeight = FontWeight.BOLD
                and(hover) {
                    backgroundColor += orange
                }
            }
        }

        // TABLE
        // =============================================================================================================

        tableRowCell {
            and(hover) {
                backgroundColor += orangeBright
            }
            and(selected) {
                backgroundColor += orange
            }
            and(empty) {
                backgroundColor += white
            }
        }

        columnHeader {
            backgroundColor += orange
            label {
                fontSize = 13.px
                textFill = white
                fontWeight = FontWeight.BOLD
            }
        }

    }
}
