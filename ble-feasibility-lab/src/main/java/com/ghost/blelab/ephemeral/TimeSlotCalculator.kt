package com.ghost.blelab.ephemeral

interface TimeSlotCalculator {
    /**
     * Get the current time slot based on rotation interval.
     * Time slot = floor(currentTimeMillis / (rotationIntervalMinutes * 60 * 1000))
     */
    fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long

    /**
     * Get the start time (millis since epoch) of a given time slot.
     */
    fun getTimeSlotStartMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long

    /**
     * Get the end time (millis since epoch) of a given time slot.
     */
    fun getTimeSlotEndMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long

    /**
     * Get remaining milliseconds until next rotation.
     */
    fun millisUntilNextRotation(rotationIntervalMinutes: Int): Long
}