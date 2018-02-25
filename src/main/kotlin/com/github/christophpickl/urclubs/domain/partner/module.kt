package com.github.christophpickl.urclubs.domain.partner

import com.github.christophpickl.urclubs.persistence.domain.PartnerDao
import com.github.christophpickl.urclubs.persistence.domain.PartnerObjectDbDao
import com.google.inject.AbstractModule

class PartnerModule : AbstractModule() {
    override fun configure() {
        bind(PartnerDao::class.java).to(PartnerObjectDbDao::class.java)
        bind(PartnerService::class.java).to(PartnerServiceImpl::class.java)
    }
}
