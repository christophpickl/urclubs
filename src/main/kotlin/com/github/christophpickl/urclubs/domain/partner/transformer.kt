package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.domain.activity.toFinishedActivity
import com.github.christophpickl.urclubs.domain.activity.toFinishedActivityDbo
import com.github.christophpickl.urclubs.persistence.domain.CategoryDbo
import com.github.christophpickl.urclubs.persistence.domain.PartnerDbo
import com.github.christophpickl.urclubs.persistence.domain.RatingDbo

fun Partner.toPartnerDbo() = PartnerDbo(
    id = idDbo,
    idMyc = idMyc,
    name = name,
    note = note,
    shortName = shortName,
    rating = rating.toRatingDbo(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategoryDbo(),
    maxCredits = maxCredits.toByte(),
    linkMyclubs = linkMyclubs,
    linkPartner = linkPartner,
    addresses = addresses.toMutableList(),
    tags = tags.toMutableList(),
    finishedActivities = finishedActivities.map { it.toFinishedActivityDbo() }.toMutableList(),
    picture = picture.saveRepresentation
)

fun Rating.toRatingDbo() = when (this) {
    Rating.UNKNOWN -> RatingDbo.UNKNOWN
    Rating.BAD -> RatingDbo.BAD
    Rating.OK -> RatingDbo.OK
    Rating.GOOD -> RatingDbo.GOOD
    Rating.SUPERB -> RatingDbo.SUPERB
}

fun Category.toCategoryDbo() = when (this) {
    Category.UNKNOWN -> CategoryDbo.UNKNOWN

    Category.DANCE -> CategoryDbo.DANCE
    Category.EMS -> CategoryDbo.EMS
    Category.GYM -> CategoryDbo.GYM
    Category.HEALTH -> CategoryDbo.HEALTH
    Category.OTHER -> CategoryDbo.OTHER
    Category.PILATES -> CategoryDbo.PILATES
    Category.SPORT -> CategoryDbo.SPORT
    Category.WATER -> CategoryDbo.WATER
    Category.WORKOUT -> CategoryDbo.WORKOUT
    Category.WUSHU -> CategoryDbo.WUSHU
    Category.YOGA -> CategoryDbo.YOGA
}

fun PartnerDbo.toPartner() = Partner(
    idDbo = id,
    idMyc = idMyc,
    shortName = shortName,
    name = name,
    note = note,
    rating = rating.toRating(),
    maxCredits = maxCredits.toInt(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategory(),
    linkMyclubs = linkMyclubs,
    linkPartner = linkPartner,
    tags = tags.toList(),
    addresses = addresses.toList(),
    finishedActivities = finishedActivities.map { it.toFinishedActivity() },
    picture = PartnerImage.readFromDb(picture)
)

fun RatingDbo?.toRating() = when (this) {
    RatingDbo.UNKNOWN -> Rating.UNKNOWN
    RatingDbo.BAD -> Rating.BAD
    RatingDbo.OK -> Rating.OK
    RatingDbo.GOOD -> Rating.GOOD
    RatingDbo.SUPERB -> Rating.SUPERB
    null -> Rating.UNKNOWN
}

fun CategoryDbo?.toCategory() = when (this) {
    CategoryDbo.UNKNOWN -> Category.UNKNOWN

    CategoryDbo.DANCE -> Category.DANCE
    CategoryDbo.EMS -> Category.EMS
    CategoryDbo.GYM -> Category.GYM
    CategoryDbo.HEALTH -> Category.HEALTH
    CategoryDbo.OTHER -> Category.OTHER
    CategoryDbo.PILATES -> Category.PILATES
    CategoryDbo.SPORT -> Category.SPORT
    CategoryDbo.WATER -> Category.WATER
    CategoryDbo.WORKOUT -> Category.WORKOUT
    CategoryDbo.WUSHU -> Category.WUSHU
    CategoryDbo.YOGA -> Category.YOGA
    null -> Category.UNKNOWN
}

