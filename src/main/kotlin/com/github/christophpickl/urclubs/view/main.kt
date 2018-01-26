package com.github.christophpickl.urclubs.view

import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.Rating
import javafx.application.Application
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.FXCollections
import javafx.scene.text.FontWeight
import tornadofx.*

// https://github.com/edvin/tornadofx

fun main(args: Array<String>) {
    Application.launch(HelloWorldApp::class.java, *args)
}

class HelloWorldApp : App(HelloWorld::class, Styles::class)

class HelloWorld : View() {

    val controller: PartnersController by inject()
    val bottomView: BottomView by inject()


    override val root = vbox {
        label("hello urclubs")
        tableview(controller.partners) {
            column("Name", Partner::name)
            column("Rating", Partner::rating)
            columnResizePolicy = SmartResize.POLICY
        }
        button("reload").setOnAction {
            controller.reloadPartners()
        }
    }

    init {
        root += bottomView
    }

}

class BottomView : View() {

    val counter = SimpleIntegerProperty()

    override val root = vbox {

        label {
            bind(counter)
            style { fontSize = 25.px }
        }
        button("Increment").setOnAction {
            increment()
        }
    }

    private fun increment() {
        counter.value += 1
    }
}

/*
class MasterView : View() {
   override val root = BorderPane()
   val detail: DetailView by inject()

   init {
      // Enable communication between the views
      detail.master = this

      // Assign the DetailView root node to the center property of the BorderPane
      root.center = detail.root

      // Find the HeaderView and assign it to the BorderPane top (alternative approach)
      root.top = find(HeaderView::class)
   }
}
 */
class PartnersController : Controller() {

    // customerTable.asyncItems { controller.loadCustomers() }

    val partners = FXCollections.observableArrayList<Partner>()

    fun reloadPartners() {
        runAsync {
            listPartners()
        } ui {
            partners.setAll(it)
        }
    }

    private fun listPartners(): List<Partner> =
            listOf(
                    Partner(idDbo = 1, idMyc = "myc1", shortName = "tj", name = "Taiji", rating = Rating.GOOD),
                    Partner(idDbo = 2, idMyc = "myc2", shortName = "ems", name = "EMS", rating = Rating.OK)
            ).apply {
                Thread.sleep(1000)
            }
}

class Styles : Stylesheet() {
    init {
        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }
    }
}
