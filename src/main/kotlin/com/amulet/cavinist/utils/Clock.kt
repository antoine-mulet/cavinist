package com.amulet.cavinist.utils

import org.springframework.stereotype.Component
import java.time.LocalDateTime

sealed class Clock {

    abstract fun getCurrentTime(): LocalDateTime

    abstract fun getCurrentTimeMillis(): Long
}

@Component
object DefaultClock : Clock() {

    override fun getCurrentTime(): LocalDateTime = LocalDateTime.now()

    override fun getCurrentTimeMillis(): Long = System.currentTimeMillis()
}