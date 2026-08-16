package com.ghost.blelab.ephemeral

import kotlinx.serialization.Serializable

@Serializable
data class EphemeralIdConfig(
    val rotationIntervalMinutes: Int = 10,
    val ephemeralIdLength: Int = 16,
    val dailyKeyLength: Int = 16
)

interface EphemeralIdGenerator {
    /**
     * Generate a new daily tracing key (16 bytes, cryptographically random).
     * This should be generated once per day and kept secret.
     */
    fun generateDailyKey(): ByteArray

    /**
     * Derive an ephemeral ID (Rolling Proximity Identifier) from a daily key and time slot.
     * Uses HKDF-SHA256 as per GAEN specification.
     * 
     * @param dailyKey 16-byte daily tracing key
     * @param timeSlot Current time slot (floor of currentTime / rotationInterval)
     * @return 16-byte ephemeral ID
     */
    fun deriveEphemeralId(dailyKey: ByteArray, timeSlot: Long): ByteArray

    /**
     * Get the current ephemeral ID using the current time slot.
     * Convenience method combining time slot calculation and derivation.
     */
    fun getCurrentEphemeralId(dailyKey: ByteArray, rotationIntervalMinutes: Int): ByteArray

    /**
     * Get the configured rotation interval in minutes.
     */
    fun getRotationIntervalMinutes(): Int

    /**
     * Set the rotation interval in minutes.
     * Must be positive.
     */
    fun setRotationIntervalMinutes(minutes: Int)
}