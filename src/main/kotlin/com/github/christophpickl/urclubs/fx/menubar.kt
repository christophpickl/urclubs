package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.UrclubsConfiguration
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerService
import com.github.christophpickl.urclubs.domain.partner.toPartnerDbo
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequestFXEvent
import com.github.christophpickl.urclubs.myclubs.MyClubsApi
import com.github.christophpickl.urclubs.myclubs.cache.MyClubsCacheManager
import com.github.christophpickl.urclubs.persistence.domain.deleteAllPartners
import com.github.christophpickl.urclubs.persistence.transactional
import com.github.christophpickl.urclubs.service.QuitListener
import com.github.christophpickl.urclubs.service.QuitManager
import javafx.scene.control.MenuBar
import tornadofx.*
import javax.persistence.EntityManager

object ShowAboutFXEvent : FXEvent()

class MenuBarController : Controller(), QuitListener {
    private val logg = LOG {}

    private val em: EntityManager by di()
    private val cacheManager: MyClubsCacheManager by di()
    private val partnerService: PartnerService by di()
    private val quitManager: QuitManager by di()
    private val myClubsApi: MyClubsApi by di()

    init {
        quitManager.addQuitListener(this)
    }

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

    fun executeReport() {
        partnerService.readAll(includeIgnored = true).also { partners ->
            println("All tags:")
            println(partners.flatMap { it.tags }.distinct().sorted().joinToString())

            println("Partners with tags:")
            partners.forEach {
                println(String.format("%-50s ... %s", it.name, it.tagsFormatted))
            }
        }
    }

    fun dummyCache() {
        myClubsApi.partner("vienna-city-bootcamp")
    }

    override fun onQuit() {
    }
}

class MyMenuBar(
    private val controller: MenuBarController
) : MenuBar() {

    init {
        if (UrclubsConfiguration.IS_MAC) {
            menu("IGNORED") {
                item("will be in app")
            }
        }
        menu("Application") {
            if (UrclubsConfiguration.IS_NOT_MAC) {
                item("About").action {
                    controller.doFire(ShowAboutFXEvent)
                }
                separator()
            }
            item("Resync Data").action {
                controller.doFire(SyncRequestFXEvent)
            }
            item("Clear Caches").action {
                controller.clearCaches()
            }
            if (UrclubsConfiguration.IS_NOT_MAC) {
                separator()
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
                item("Print DB Report").action {
                    controller.executeReport()
                }
                item("Dummy cache").action {
                    controller.dummyCache()
                }
            }
        }
    }
}
