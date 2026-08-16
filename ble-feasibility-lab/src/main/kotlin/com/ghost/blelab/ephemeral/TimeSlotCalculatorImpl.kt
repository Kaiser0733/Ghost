package com.ghost.blelab.ephemeral

import com.ghost.blelab.util.TimeUtil

class TimeSlotCalculatorImpl : TimeSlotCalculator {

    override fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long {
        require(rotationIntervalMinutes > 0) { "Rotation interval must be positive" }
        val intervalMillis = rotationIntervalMinutes.toLong() * 60 * 1000
        return TimeUtil.nowMillis() / intervalMillis
    }

    override fun getTimeSlotStartMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long {
        require(rotationIntervalMinutes > 0) { "Rotation interval must be positive" }
        val intervalMillis = rotationIntervalMinutes.toLong() * 60 * 1000
        return timeSlot * intervalMillis
    }

    override fun getTimeSlotEndMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long {
        return getTimeSlotStartMillis(timeSlot, rotationIntervalMinutes) + 
            rotationIntervalMinutes.toLong() * 60 * 1000 - 1
    }

    override fun millisUntilNextRotation(rotationIntervalMinutes: Int): Long {
        require(rotationIntervalMinutes > 0) { "Rotation interval must be positive" }
        val intervalMillis = rotationIntervalMinutes.toLong() * 60 * 1000
        val now = TimeUtil.nowMillis()
        return intervalMillis - (now % intervalMillis)
    }
}