package com.github.christophpickl.urclubs.domain.partner

fun Partner.toPartnerDbo() = PartnerDbo(
        id = idDbo,
        idMyc = idMyc,
        name = name,
        shortName = shortName,
        rating = rating.toRatingDbo(),
        deletedByMyc = deletedByMyc
)

fun Rating.toRatingDbo() = when(this) {
    Rating.UNKNOWN -> RatingDbo.UNKNOWN
    Rating.BAD -> RatingDbo.BAD
    Rating.OK -> RatingDbo.OK
    Rating.GOOD -> RatingDbo.GOOD
    Rating.SUPERB -> RatingDbo.SUPERB
}

fun PartnerDbo.toPartner() = Partner(
        idDbo = id,
        idMyc = idMyc,
        name = name,
        shortName = shortName,
        rating = rating.toRating(),
        deletedByMyc = deletedByMyc
)

fun RatingDbo.toRating() = when(this) {
    RatingDbo.UNKNOWN -> Rating.UNKNOWN
    RatingDbo.BAD -> Rating.BAD
    RatingDbo.OK -> Rating.OK
    RatingDbo.GOOD -> Rating.GOOD
    RatingDbo.SUPERB -> Rating.SUPERB
}
