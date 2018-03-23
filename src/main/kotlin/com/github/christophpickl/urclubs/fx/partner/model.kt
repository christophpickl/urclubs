package com.github.christophpickl.urclubs.fx.partner

import com.github.christophpickl.urclubs.domain.partner.Category
import com.github.christophpickl.urclubs.domain.partner.Partner
import com.github.christophpickl.urclubs.domain.partner.PartnerImage
import com.github.christophpickl.urclubs.domain.partner.Rating
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class CurrentPartnerFx() : ViewModel() {

    lateinit var original: Partner

    val name = SimpleStringProperty()
    val tags = SimpleStringProperty()
    val note = SimpleStringProperty()
    val category = SimpleObjectProperty(Category.UNKNOWN)
    val rating = SimpleObjectProperty(Rating.UNKNOWN)
    val favourited = SimpleBooleanProperty()
    val wishlisted = SimpleBooleanProperty()
    val maxCredits = SimpleIntegerProperty()
    val totalVisits = SimpleIntegerProperty()
    val creditsLeftThisPeriod = SimpleIntegerProperty()

    val shortName = SimpleStringProperty()
    val linkMyclubs = SimpleStringProperty()
    val linkPartner = SimpleStringProperty()

    val picture = SimpleObjectProperty<javafx.scene.image.Image>(PartnerImage.DefaultPicture.fxImage)
    val pictureWrapper = SimpleObjectProperty<PartnerImage>(PartnerImage.DefaultPicture)

    fun toPartner() = original.copy(
        name = name.get(),
        note = note.get(),
        category = category.get(),
        rating = rating.get(),
        favourited = favourited.get(),
        wishlisted = wishlisted.get(),
        maxCredits = maxCredits.get(),
        picture = pictureWrapper.get()
    )

    fun initPartner(partner: Partner) {
        original = partner
        name.set(partner.name)
        note.set(partner.note)
        category.set(partner.category)
        rating.set(partner.rating)
        favourited.set(partner.favourited)
        wishlisted.set(partner.wishlisted)
        maxCredits.set(partner.maxCredits)
        tags.set(partner.tagsFormatted)
        picture.set(partner.picture.fxImage)
        pictureWrapper.set(partner.picture)

        shortName.set(partner.shortName)
        linkMyclubs.set(partner.linkMyclubs)
        linkPartner.set(partner.linkPartner)

        totalVisits.set(partner.finishedActivities.size)
        creditsLeftThisPeriod.set(partner.creditsLeftThisPeriod)
    }
}
