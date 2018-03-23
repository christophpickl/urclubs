package mains

import com.github.christophpickl.urclubs.fx.MainView
import javafx.application.Application

//    PartnerView::class
fun main(args: Array<String>) {
    viewClass = MainView::class
    Application.launch(DummyApp::class.java, *args)
}
