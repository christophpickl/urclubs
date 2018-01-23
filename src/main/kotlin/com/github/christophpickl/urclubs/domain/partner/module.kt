package com.github.christophpickl.urclubs.domain.partner

import com.google.inject.AbstractModule

class PartnerModule : AbstractModule() {
    override fun configure() {
        bind(PartnerDao::class.java).to(PartnerObjectDbDao::class.java)
        bind(PartnerService::class.java).to(PartnerServiceImpl::class.java)
    }
}
