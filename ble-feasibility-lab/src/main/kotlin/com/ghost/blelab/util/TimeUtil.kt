package com.ghost.blelab.util

import java.time.Instant
import java.time.Duration
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object TimeUtil {
    
    private const val SECONDS_PER_MINUTE = 60L
    private const val MILLIS_PER_SECOND = 1000L
    private const val MILLIS_PER_MINUTE = SECONDS_PER_MINUTE * MILLIS_PER_SECOND
    private const val MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE
    private const val MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR

    /**
     * Current time in milliseconds since epoch.
     */
    fun nowMillis(): Long = System.currentTimeMillis()

    /**
     * Current time in nanoseconds (for high-resolution timing).
     */
    fun nowNanos(): Long = System.nanoTime()

    /**
     * Format duration in milliseconds to human-readable string.
     */
    fun formatDuration(millis: Long): String {
        if (millis < 0) return "0ms"
        val absMillis = millis
        val days = absMillis / MILLIS_PER_DAY
        val hours = (absMillis % MILLIS_PER_DAY) / MILLIS_PER_HOUR
        val minutes = (absMillis % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE
        val seconds = (absMillis % MILLIS_PER_MINUTE) / MILLIS_PER_SECOND
        val ms = absMillis % MILLIS_PER_SECOND
        
        return buildString {
            if (days > 0L) append("${days}d ")
            if (hours > 0L) append("${hours}h ")
            if (minutes > 0L) append("${minutes}m ")
            if (seconds > 0L || (days == 0L && hours == 0L && minutes == 0L)) append("${seconds}s ")
            if (ms > 0L && days == 0L && hours == 0L && minutes == 0L && seconds == 0L) append("${ms}ms")
        }.trim()
    }

    /**
     * Format timestamp to ISO-8601 string.
     */
    fun formatTimestamp(timestampMillis: Long): String = Instant.ofEpochMilli(timestampMillis)
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

    /**
     * Parse ISO-8601 timestamp to milliseconds.
     */
    fun parseTimestamp(isoString: String): Long = Instant.parse(isoString).toEpochMilli()

    /**
     * Calculate time slot for rotating identifier.
     * Time slot = floor(currentTimeMillis / (rotationIntervalMinutes * 60 * 1000))
     */
    fun getCurrentTimeSlot(rotationIntervalMinutes: Int): Long {
        require(rotationIntervalMinutes > 0) { "Rotation interval must be positive" }
        val intervalMillis = rotationIntervalMinutes.toLong() * MILLIS_PER_MINUTE
        return nowMillis() / intervalMillis
    }

    /**
     * Get the start time (millis) of a given time slot.
     */
    fun getTimeSlotStartMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long {
        require(rotationIntervalMinutes > 0) { "Rotation interval must be positive" }
        val intervalMillis = rotationIntervalMinutes.toLong() * MILLIS_PER_MINUTE
        return timeSlot * intervalMillis
    }

    /**
     * Get the end time (millis) of a given time slot.
     */
    fun getTimeSlotEndMillis(timeSlot: Long, rotationIntervalMinutes: Int): Long {
        return getTimeSlotStartMillis(timeSlot, rotationIntervalMinutes) + 
            rotationIntervalMinutes.toLong() * MILLIS_PER_MINUTE - 1
    }

    /**
     * Get remaining milliseconds until next rotation.
     */
    fun millisUntilNextRotation(rotationIntervalMinutes: Int): Long {
        val intervalMillis = rotationIntervalMinutes.toLong() * MILLIS_PER_MINUTE
        val now = nowMillis()
        return intervalMillis - (now % intervalMillis)
    }

    /**
     * Format elapsed time since startMillis.
     */
    fun formatElapsedSince(startMillis: Long): String = formatDuration(nowMillis() - startMillis)

    /**
     * Convert nanoseconds to milliseconds.
     */
    fun nanosToMillis(nanos: Long): Long = nanos / 1_000_000
}