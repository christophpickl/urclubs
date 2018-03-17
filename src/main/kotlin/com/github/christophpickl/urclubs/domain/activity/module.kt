package com.github.christophpickl.urclubs.domain.activity

import com.google.inject.AbstractModule

class ActivityModule : AbstractModule() {
    override fun configure() {
//        bind(ActivityDao::class.java).to(ActivityDaoImpl::class.java)
        bind(ActivityService::class.java).to(ActivityServiceImpl::class.java)
    }
}
