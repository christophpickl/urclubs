package com.github.christophpickl.urclubs.service

import com.github.christophpickl.urclubs.service.sync.PartnerSyncer
import com.google.inject.AbstractModule

class ServiceModule(private val args: Array<String>) : AbstractModule() {
    override fun configure() {
        bind(Credentials::class.java).toProvider(CredentialsProvider(args))
        bind(PartnerSyncer::class.java)
    }
}
