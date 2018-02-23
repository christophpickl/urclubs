package com.github.christophpickl.urclubs.domain.activity

import com.github.christophpickl.urclubs.persistence.HasId
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
data class ActivityDbo(

        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        override val id: Long,

        @Column(nullable = false, unique = true)
        var idMyc: String
) : HasId {
    companion object
}
