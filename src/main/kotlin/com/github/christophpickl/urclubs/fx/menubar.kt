package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.Rating
import javafx.scene.control.MenuBar
import tornadofx.*
import java.util.concurrent.atomic.AtomicInteger

class MenuBarController : Controller() {

    private val logg = LOG {}
    private val partnerService: PartnerService by di()
    private val counter = AtomicInteger()

    fun createDummyData() {
        logg.info { "createDummyData()" }
        listOf(
            dummyPartner().copy(
                name = "Dummy EMS",
                category = Category.EMS,
                rating = Rating.SUPERB
            ),
            dummyPartner().copy(
                name = "Dummy Yoga",
                category = Category.YOGA
            )
        ).forEach {
            partnerService.create(it)
        }
    }

    private fun dummyPartner(): Partner {
        val count = counter.incrementAndGet()
        return Partner.prototype().copy(
            idMyc = "dummy$count",
            shortName = "dummy$count"
        )
    }

}

class MyMenuBar(private val menuBarController: MenuBarController) : MenuBar() {

    init {
        menu("Develop") {
            item("Create Dummy Data").action {
                menuBarController.createDummyData()
            }
        }
    }
}
