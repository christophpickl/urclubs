package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.Partner
import com.github.christophpickl.urclubs.Rating
import com.github.christophpickl.urclubs.persistence.PartnerDao
import com.github.christophpickl.urclubs.persistence.PartnerDbo
import com.github.christophpickl.urclubs.persistence.RatingDbo
import javax.inject.Inject

interface PartnerService {
    fun create(partner: Partner): Partner
    fun readAll(): List<Partner>
    fun read(id: Long): Partner?
    fun update(partner: Partner)
    fun delete(partner: Partner)
}

class PartnerServiceImpl @Inject constructor(
        private val partnerDao: PartnerDao
): PartnerService {
    override fun create(partner: Partner) =
            partnerDao.create(partner.toPartnerDbo()).toPartner()

    override fun readAll() =
            partnerDao.readAll().map { it.toPartner() }

    override fun read(id: Long) =
            partnerDao.read(id)?.toPartner()

    override fun update(partner: Partner) {
        partnerDao.update(partner.toPartnerDbo())
    }

    override fun delete(partner: Partner) {
        partnerDao.delete(partner.toPartnerDbo())
    }
}

fun Partner.toPartnerDbo() = PartnerDbo(
        id = idDbo,
        idMyc = idMyc,
        name = name,
        rating = rating.toRatingDbo()
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
        rating = rating.toRating()
)

fun RatingDbo.toRating() = when(this) {
    RatingDbo.UNKNOWN -> Rating.UNKNOWN
    RatingDbo.BAD -> Rating.BAD
    RatingDbo.OK -> Rating.OK
    RatingDbo.GOOD -> Rating.GOOD
    RatingDbo.SUPERB -> Rating.SUPERB
}
