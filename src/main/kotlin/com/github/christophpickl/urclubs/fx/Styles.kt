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
        val showHandOnHover by cssclass()

        // GENERAL COMPONENTS
        val mainPanel by cssclass()
        val partnerDetailPanel by cssclass()

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

            // TODO proper row cols
            val rowColWhiteEven = c("#3B3843")
            val rowColWhiteOdd = c("#3B3843")
            val rowColBlackEven = c("#3B3843")
            val rowColBlackOdd = c("#3B3843")
        }
    }

    init {

        val onHoverShowHandCursorMixin = mixin {
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

        partnerDetailPanel {
            label {
                textFill = white
            }
            field {
                textFill = white // TODO has got no effect!
//                fontWeight = FontWeight.BOLD
            }
        }

        showHandOnHover {
            +onHoverShowHandCursorMixin
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

        val evenOddGrey = mixin {
            and(even) {
                backgroundColor += greyVeryBright
            }
            and(odd) {
                backgroundColor += greyBright
            }
        }

        val theBigThree = mixin {
            +flat
            +orangeBorder
            +backgroundHoverColors
        }

        contextMenu {
            backgroundColor += greyDark
            menuItem {
                and(hover) {
                    backgroundColor += orange
                }
                label {
                    textFill = white
                }
            }
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
            +onHoverShowHandCursorMixin
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
                textFill = white
                fontWeight = FontWeight.BOLD
                and(even) {
                    backgroundColor += greyVeryBright
                }
                and(odd) {
                    backgroundColor += greyBright
                }
                and(hover) {
                    // order matters!
                    backgroundColor += orange
                }
            }
        }

        // TABLE
        // =============================================================================================================

        tableRowCell {
            and(even) {
                // backgroundColor += javafx.scene.paint.Color.GREENYELLOW
            }
            and(odd) {
                // backgroundColor += javafx.scene.paint.Color.AQUA
            }
            and(hover) {
                backgroundColor += orangeBright
            }
            and(selected) {
                backgroundColor += orange
            }
            and(empty) {
                backgroundColor += white
                tableCell {
                    borderColor += box(javafx.scene.paint.Color.WHITE)
                }
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
