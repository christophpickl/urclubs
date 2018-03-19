package com.github.christophpickl.urclubs.persistence.domain

import com.github.christophpickl.urclubs.persistence.COL_LENGTH_MED
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class FinishedActivityDbo(

    @Column(nullable = false, length = COL_LENGTH_MED)
    var title: String,

    @Column(nullable = false)
    var date: LocalDateTime

) {
    companion object
}
