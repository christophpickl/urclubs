package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.IS_DEVELOPMENT
import com.github.christophpickl.urclubs.IS_MAC
import com.github.christophpickl.urclubs.QuitFXEvent
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.toPartnerDbo
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.persistence.createCriteriaDeleteAll
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.transactional
import javafx.scene.control.MenuBar
import tornadofx.*
import javax.persistence.EntityManager

object ShowAboutFXEvent : FXEvent()

class MenuBarController : Controller() {

    private val logg = LOG {}
    private val em: EntityManager by di()

    fun resetDummyData() {
        logg.info { "resetDummyData()" }

        em.transactional {
            createQuery(createCriteriaDeleteAll<PartnerDbo>()).executeUpdate()
            Partner.Dummies.all.forEach {
                persist(it.toPartnerDbo())
            }
        }
        fire(PartnerListRequestFXEvent)
    }

    fun quit() {
        fire(QuitFXEvent)
    }

    fun showAbout() {
        fire(ShowAboutFXEvent)
    }
}

class MyMenuBar(
    private val controller: MenuBarController
) : MenuBar() {

    init {

        menu("Application") {
            item("About").action {
                controller.showAbout()
            }
            if (!IS_MAC) {
                item("Quit").action {
                    controller.quit()
                }
            }
        }
        if (IS_DEVELOPMENT) {
            menu("Develop") {
                item("Reset Dummy Data").action {
                    controller.resetDummyData()
                }
            }
        }
    }
}
