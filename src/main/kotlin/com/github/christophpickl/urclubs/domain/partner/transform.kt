package com.github.christophpickl.urclubs.domain.partner

fun Partner.toPartnerDbo() = PartnerDbo(
    id = idDbo,
    idMyc = idMyc,
    name = name,
    shortName = shortName,
    address = address,
    rating = rating.toRatingDbo(),
    deletedByMyc = deletedByMyc,
    favourited = favourited,
    wishlisted = wishlisted,
    ignored = ignored,
    category = category.toCategoryDbo(),
    linkMyclubsSite = linkMyclubsSite,
    linkPartnerSite = linkPartnerSite
)

fun Rating.toRatingDbo() = when (this) {
    Rating.UNKNOWN -> RatingDbo.UNKNOWN
    Rating.BAD -> RatingDbo.BAD
    Rating.OK -> RatingDbo.OK
    Rating.GOOD -> RatingDbo.GOOD
    Rating.SUPERB -> RatingDbo.SUPERB
}

fun Category.toCategoryDbo() = when (this) {
    Category.EMS -> CategoryDbo.EMS
    Category.GYM -> CategoryDbo.GYM
    Category.YOGA -> CategoryDbo.YOGA
    Category.WUSHU -> CategoryDbo.WUSHU
    Category.WORKOUT -> CategoryDbo.WORKOUT
    Category.HEALTH -> CategoryDbo.HEALTH
    Category.OTHER -> CategoryDbo.OTHER
    Category.UNKNOWN -> CategoryDbo.UNKNOWN
}

fun PartnerDbo.toPartner() = Partner(
    idDbo = id,
    idMyc = idMyc,
    shortName = shortName,
    name = name ?: "",
    address = address ?: "",
    rating = rating.toRating(),
    deletedByMyc = deletedByMyc ?: false,
    favourited = favourited ?: false,
    wishlisted = wishlisted ?: false,
    ignored = ignored ?: false,
    category = category.toCategory(),
    linkMyclubsSite = linkMyclubsSite ?: "",
    linkPartnerSite = linkPartnerSite ?: ""
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
    CategoryDbo.EMS -> Category.EMS
    CategoryDbo.GYM -> Category.GYM
    CategoryDbo.YOGA -> Category.YOGA
    CategoryDbo.WUSHU -> Category.WUSHU
    CategoryDbo.WORKOUT -> Category.WORKOUT
    CategoryDbo.HEALTH -> Category.HEALTH
    CategoryDbo.OTHER -> Category.OTHER
    CategoryDbo.UNKNOWN -> Category.UNKNOWN
    null -> Category.UNKNOWN
}
