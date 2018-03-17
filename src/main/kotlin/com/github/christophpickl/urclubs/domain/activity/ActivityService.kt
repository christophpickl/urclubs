package com.github.christophpickl.urclubs.domain.activity

interface ActivityService {
    fun readAllFinished(): List<FinishedActivity>
}

class ActivityServiceImpl : ActivityService {

    override fun readAllFinished(): List<FinishedActivity> {
        return emptyList()
    }

}
