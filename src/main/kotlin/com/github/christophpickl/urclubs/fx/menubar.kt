package com.github.christophpickl.urclubs.fx

import com.github.christophpickl.kpotpourri.common.logging.LOG
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.toPartnerDbo
import com.github.christophpickl.urclubs.fx.partner.PartnerListRequest
import com.github.christophpickl.urclubs.persistence.createCriteriaDeleteAll
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.transactional
import javafx.scene.control.MenuBar
import tornadofx.*
import javax.persistence.EntityManager

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
        fire(PartnerListRequest)
    }

}

class MyMenuBar(private val menuBarController: MenuBarController) : MenuBar() {

    init {
        menu("Develop") {
            item("Reset Dummy Data").action {
                menuBarController.resetDummyData()
            }
        }
    }
}
