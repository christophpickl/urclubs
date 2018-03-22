package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.toPartnerDbo
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.myclubs.cache.MyClubsCacheManager
import com.github.christophpickl.urclubs.persistence.domain.deleteAllPartners
import com.github.christophpickl.urclubs.persistence.transactional
import javafx.scene.control.MenuBar
import tornadofx.*
import javax.persistence.EntityManager

object ShowAboutFXEvent : FXEvent()

class MenuBarController : Controller() {

    private val logg = LOG {}
    private val em: EntityManager by di()
    private val cacheManager: MyClubsCacheManager by di()

    fun resetDummyData() {
        logg.info { "resetDummyData()" }

        em.transactional {
            deleteAllPartners()
            Partner.Dummies.all.forEach {
                persist(it.toPartnerDbo())
            }
        }
        fire(PartnerListRequestFXEvent)
    }

    fun <T : FXEvent> doFire(event: T) {
        fire(event)
    }
    fun clearCaches() {
        cacheManager.clearCaches()
    }
}

class MyMenuBar(
    private val controller: MenuBarController
) : MenuBar() {

    init {
        useSystemMenuBarProperty().set(true)
        menu("Application") {
            item("Clear Caches").action {
                controller.clearCaches()
            }
            item("About").action {
                controller.doFire(ShowAboutFXEvent)
            }
            if (UrclubsConfiguration.IS_NOT_MAC) {
                item("Quit").action {
                    controller.doFire(QuitFXEvent)
                }
            }
        }
        if (UrclubsConfiguration.IS_DEVELOPMENT) {
            menu("Develop") {
                item("Reset Dummy Data").action {
                    controller.resetDummyData()
                }
                item("Reload DB").action {
                    controller.doFire(PartnerListRequestFXEvent)
                }
            }
        }
    }
}
