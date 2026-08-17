package com.ghost.blelab.ble.advertiser

import com.ghost.blelab.ble.common.BleConstants
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for AdvertisePayload - pure logic, no Bluetooth radio.
 * Tests payload serialization/deserialization and format validation.
 */
class AdvertisePayloadTest {

    private val testEphemeralId = ByteArray(BleConstants.EPHEMERAL_ID_LENGTH) { (it + 1).toByte() }

    @Test
    fun testToServiceDataCorrectLength() {
        val payload = AdvertisePayload(ephemeralId = testEphemeralId)
        val serviceData = payload.toServiceData()

        assertEquals("Service data should be 17 bytes (1 protocol + 16 ephemeral ID)",
            1 + BleConstants.EPHEMERAL_ID_LENGTH, serviceData.size)
    }

    @Test
    fun testToServiceDataProtocolVersion() {
        val payload = AdvertisePayload(ephemeralId = testEphemeralId)
        val serviceData = payload.toServiceData()

        assertEquals("First byte should be protocol version",
            BleConstants.PROTOCOL_VERSION, serviceData[0])
    }

    @Test
    fun testToServiceDataEphemeralIdCorrect() {
        val payload = AdvertisePayload(ephemeralId = testEphemeralId)
        val serviceData = payload.toServiceData()

        val extractedId = serviceData.copyOfRange(1, 1 + BleConstants.EPHEMERAL_ID_LENGTH)
        assertArrayEquals("Ephemeral ID should match", testEphemeralId, extractedId)
    }

    @Test
    fun testFromServiceDataValidPayload() {
        val payload = AdvertisePayload(ephemeralId = testEphemeralId)
        val serviceData = payload.toServiceData()

        val parsed = AdvertisePayload.fromServiceData(serviceData)

        assertNotNull("Should parse valid payload", parsed)
        assertEquals("Protocol version should match", BleConstants.PROTOCOL_VERSION, parsed!!.protocolVersion)
        assertArrayEquals("Ephemeral ID should match", testEphemeralId, parsed.ephemeralId)
    }

    @Test
    fun testFromServiceDataWrongLength() {
        // Too short
        val shortData = ByteArray(10)
        assertNull("Should reject too-short data", AdvertisePayload.fromServiceData(shortData))

        // Too long
        val longData = ByteArray(20)
        assertNull("Should reject too-long data", AdvertisePayload.fromServiceData(longData))
    }

    @Test
    fun testFromServiceDataWrongProtocolVersion() {
        val payload = AdvertisePayload(ephemeralId = testEphemeralId)
        val serviceData = payload.toServiceData()

        // Modify protocol version
        val modifiedData = serviceData.copyOf()
        modifiedData[0] = 99.toByte()

        assertNull("Should reject unknown protocol version", AdvertisePayload.fromServiceData(modifiedData))
    }

    @Test
    fun testToServiceDataEmptyEphemeralIdThrows() {
        val emptyId = ByteArray(0)
        val payload = AdvertisePayload(ephemeralId = emptyId)

        try {
            payload.toServiceData()
            fail("Should throw for wrong ephemeral ID length")
        } catch (e: IllegalArgumentException) {
            assertTrue("Error message should mention length", e.message!!.contains("16"))
        }
    }

    @Test
    fun testFromServiceDataNullInput() {
        // Note: Kotlin won't let us pass null to ByteArray parameter
        // but we can test with an array of wrong size
        assertNull("Should handle edge cases gracefully", AdvertisePayload.fromServiceData(ByteArray(0)))
    }

    @Test
    fun testRoundTrip() {
        val original = AdvertisePayload(
            protocolVersion = BleConstants.PROTOCOL_VERSION,
            ephemeralId = testEphemeralId
        )

        val serviceData = original.toServiceData()
        val parsed = AdvertisePayload.fromServiceData(serviceData)

        assertNotNull("Round-trip should succeed", parsed)
        assertEquals("Protocol version round-trip", original.protocolVersion, parsed!!.protocolVersion)
        assertArrayEquals("Ephemeral ID round-trip", original.ephemeralId, parsed.ephemeralId)
    }
}