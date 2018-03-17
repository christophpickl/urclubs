package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.kpotpourri.common.logging.LOG

interface ActivityService {

    fun readAllFinished(): List<FinishedActivity>
    // save is done via Partner.finishedActivities
}

class ActivityServiceImpl : ActivityService {

    private val log = LOG {}

    override fun readAllFinished(): List<FinishedActivity> {
        log.debug { "readAllFinished()" }
        return emptyList()
    }

}
