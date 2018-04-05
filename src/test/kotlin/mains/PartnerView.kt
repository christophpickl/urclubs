package mains

import com.github.christophpickl.urclubs.fx.partner.detail.PartnerDetailView
import javafx.application.Application

fun main(args: Array<String>) {
    viewClass = PartnerDetailView::class
    Application.launch(DummyApp::class.java, *args)
}

