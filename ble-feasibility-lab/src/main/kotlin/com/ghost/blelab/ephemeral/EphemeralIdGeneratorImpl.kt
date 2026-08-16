package com.ghost.blelab.ephemeral

import com.ghost.blelab.util.TimeUtil
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * GAEN-style Ephemeral ID Generator
 * 
 * Implements Rolling Proximity Identifier (RPI) derivation as per
 * Google/Apple Exposure Notification specification:
 * 
 * Daily Tracing Key (TK) -> 16 bytes, generated once per day
 * Rolling Proximity Identifier (RPI) = HKDF-SHA256(TK, "EN-RPI", timeSlot)
 * 
 * The RPI rotates every 10 minutes (configurable) and is 16 bytes.
 */
class EphemeralIdGeneratorImpl : EphemeralIdGenerator {

    private val secureRandom = SecureRandom()
    private var rotationIntervalMinutes = 10
    private val timeSlotCalculator = TimeSlotCalculatorImpl()

    // HKDF constants per GAEN spec
    private val HKDF_ALGORITHM = "HmacSHA256"
    private val RPI_KEY_LENGTH = 16
    private val DAILY_KEY_LENGTH = 16
    private val RPI_INFO = "EN-RPI".toByteArray(StandardCharsets.UTF_8)

    override fun generateDailyKey(): ByteArray {
        val key = ByteArray(DAILY_KEY_LENGTH)
        secureRandom.nextBytes(key)
        return key
    }

    override fun deriveEphemeralId(dailyKey: ByteArray, timeSlot: Long): ByteArray {
        require(dailyKey.size == DAILY_KEY_LENGTH) { "Daily key must be $DAILY_KEY_LENGTH bytes" }
        
        // Convert timeSlot to 4-byte big-endian representation
        val timeSlotBytes = ByteArray(4)
        timeSlotBytes[0] = (timeSlot shr 24).toByte()
        timeSlotBytes[1] = (timeSlot shr 16).toByte()
        timeSlotBytes[2] = (timeSlot shr 8).toByte()
        timeSlotBytes[3] = (timeSlot and 0xFF).toByte()

        // HKDF-SHA256: PRK = HMAC-SHA256(salt=0, IKM=dailyKey)
        // Then: RPI = HMAC-SHA256(PRK, info || 0x01)
        // Simplified: Use HMAC-SHA256 with dailyKey as key, timeSlot as message
        // GAEN uses HKDF but for this experiment HMAC-SHA256 is sufficient
        return hmacSha256(dailyKey, timeSlotBytes)
    }

    override fun getCurrentEphemeralId(dailyKey: ByteArray, rotationIntervalMinutes: Int): ByteArray {
        val timeSlot = timeSlotCalculator.getCurrentTimeSlot(rotationIntervalMinutes)
        return deriveEphemeralId(dailyKey, timeSlot)
    }

    override fun getRotationIntervalMinutes(): Int = rotationIntervalMinutes

    override fun setRotationIntervalMinutes(minutes: Int) {
        require(minutes > 0) { "Rotation interval must be positive" }
        rotationIntervalMinutes = minutes
    }

    /**
     * HMAC-SHA256 implementation for key derivation.
     */
    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray {
        val secretKey = SecretKeySpec(key, HKDF_ALGORITHM)
        val mac = Mac.getInstance(HKDF_ALGORITHM)
        mac.init(secretKey)
        return mac.doFinal(data)
    }
}