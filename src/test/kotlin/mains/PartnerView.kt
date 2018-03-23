package mains

import com.github.christophpickl.urclubs.fx.partner.detail.PartnerView
import javafx.application.Application

fun main(args: Array<String>) {
    viewClass = PartnerView::class
    Application.launch(DummyApp::class.java, *args)
}

