package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*

class CurrentPartnerFx() : ViewModel() {

    private lateinit var original: Partner // TODO nasty
    val name = SimpleStringProperty()
    val category = SimpleObjectProperty(Category.UNKNOWN)

    fun toPartner() = original.copy(
        name = name.get(),
        category = category.get()
    )

    fun initPartner(partner: Partner) {
        original = partner
        name.set(partner.name)
        category.set(partner.category)
    }
}

class PartnersFx : ViewModel() {
    val partners = FXCollections.observableArrayList<PartnerFx>()
}

class PartnerFx {

    // val timeProperty = SimpleObjectProperty(LocalDateTime.now())
//    val name = SimpleStringProperty()

}

//fun Partner.toPartnerFx() = PartnerFx(this).apply {
//    name.set(this@toPartnerFx.name)
//    category.set(this@toPartnerFx.category)
//}
