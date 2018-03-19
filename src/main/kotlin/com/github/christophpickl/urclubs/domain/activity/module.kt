package com.github.christophpickl.urclubs.domain.activity

import com.google.inject.AbstractModule

class ActivityModule : AbstractModule() {
    override fun configure() {
        bind(ActivityService::class.java).to(ActivityServiceImpl::class.java)
    }
}
