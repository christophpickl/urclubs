package com.github.christophpickl.urclubs

import com.github.christophpickl.kpotpourri.common.logging.LOG
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAccessor

// enhanced Timing as measureTimeMillis isn't good enough ;)
object Stopwatch {

    private val log = LOG {}

    fun <T> elapse(logPrefix: String, callback: () -> T): T{
        val start = System.currentTimeMillis()
        val result = callback()
        val seconds = (System.currentTimeMillis() - start) / 1000
        log.info { "$logPrefix took $seconds seconds." }
        return result
    }

}

fun TemporalAccessor.toLocalDate() = LocalDate.from(this)!!
fun TemporalAccessor.toLocalDateTime() = LocalDateTime.from(this)!!
