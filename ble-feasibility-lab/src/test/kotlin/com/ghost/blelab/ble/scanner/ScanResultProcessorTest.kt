package com.ghost.blelab.ble.scanner

import com.ghost.blelab.ble.advertiser.AdvertisePayload
import com.ghost.blelab.ble.common.BleConstants
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for ScanResultProcessor - pure logic, no Bluetooth radio.
 * Tests payload parsing, validation, and observation creation.
 */
class ScanResultProcessorTest {

    private val processor = ScanResultProcessor()
    private val validEphemeralId = ByteArray(BleConstants.EPHEMERAL_ID_LENGTH) { (it + 1).toByte() }
    private val validPayload = AdvertisePayload(ephemeralId = validEphemeralId)
    private val validServiceData = validPayload.toServiceData()

    @Test
    fun testProcessValidScanResult() {
        val scanResult = createMockScanResult(validServiceData, -65, 1000000000L)

        val result = processor.processScanResult(scanResult)

        assertTrue("Should be Valid result", result is ScanResultProcessor.ProcessResult.Valid)
        val valid = result as ScanResultProcessor.ProcessResult.Valid
        assertArrayEquals("Ephemeral ID should match", validEphemeralId, valid.ephemeralId)
        assertEquals("Protocol version should match", BleConstants.PROTOCOL_VERSION, valid.protocolVersion)
        assertEquals("RSSI should match", -65, valid.rssi)
        assertEquals("Scan timestamp should match", 1000000000L, valid.scanTimestampNanos)
        assertArrayEquals("Raw service data should match", validServiceData, valid.rawServiceData)
    }

    @Test
    fun testProcessScanResultNoServiceData() {
        val scanResult = createMockScanResult(null, -65, 1000000000L)

        val result = processor.processScanResult(scanResult)

        assertTrue("Should be Invalid result", result is ScanResultProcessor.ProcessResult.Invalid)
        val invalid = result as ScanResultProcessor.ProcessResult.Invalid
        assertTrue("Reason should mention no service data", invalid.reason.contains("No service data"))
    }

    @Test
    fun testProcessScanResultWrongServiceUuid() {
        // Create scan result with different service UUID
        val scanResult = createMockScanResultWithDifferentUuid(validServiceData, -65, 1000000000L)

        val result = processor.processScanResult(scanResult)

        assertTrue("Should be Invalid result", result is ScanResultProcessor.ProcessResult.Invalid)
        val invalid = result as ScanResultProcessor.ProcessResult.Invalid
        assertTrue("Reason should mention UUID not found", invalid.reason.contains("not found"))
    }

    @Test
    fun testProcessScanResultMalformedPayload() {
        // Create malformed service data (wrong length)
        val malformedData = ByteArray(10)
        val scanResult = createMockScanResult(malformedData, -65, 1000000000L)

        val result = processor.processScanResult(scanResult)

        assertTrue("Should be Invalid result", result is ScanResultProcessor.ProcessResult.Invalid)
        val invalid = result as ScanResultProcessor.ProcessResult.Invalid
        assertTrue("Reason should mention parse failure", invalid.reason.contains("Failed to parse"))
    }

    @Test
    fun testProcessScanResultWrongProtocolVersion() {
        // Create payload with wrong protocol version
        val wrongPayload = AdvertisePayload(protocolVersion = 99.toByte(), ephemeralId = validEphemeralId)
        val wrongServiceData = wrongPayload.toServiceData()
        val scanResult = createMockScanResult(wrongServiceData, -65, 1000000000L)

        val result = processor.processScanResult(scanResult)

        assertTrue("Should be Invalid result", result is ScanResultProcessor.ProcessResult.Invalid)
        val invalid = result as ScanResultProcessor.ProcessResult.Invalid
        assertTrue("Reason should mention unsupported protocol", invalid.reason.contains("Unsupported protocol"))
    }

    @Test
    fun testHasExperimentServiceDataTrue() {
        val scanResult = createMockScanResult(validServiceData, -65, 1000000000L)

        val result = processor.hasExperimentServiceData(scanResult)

        assertTrue("Should detect experiment service data", result)
    }

    @Test
    fun testHasExperimentServiceDataFalse() {
        val scanResult = createMockScanResult(null, -65, 1000000000L)

        val result = processor.hasExperimentServiceData(scanResult)

        assertFalse("Should not detect experiment service data", result)
    }

    @Test
    fun testHasExperimentServiceDataWrongUuid() {
        val scanResult = createMockScanResultWithDifferentUuid(validServiceData, -65, 1000000000L)

        val result = processor.hasExperimentServiceData(scanResult)

        assertFalse("Should not detect experiment service data with wrong UUID", result)
    }

    // Helper to create mock scan result with our service data
    private fun createMockScanResult(serviceData: ByteArray?, rssi: Int, timestampNanos: Long): android.bluetooth.le.ScanResult {
        val scanRecordBuilder = android.bluetooth.le.ScanRecord.Builder()
        if (serviceData != null) {
            scanRecordBuilder.setServiceData(android.os.ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID), serviceData)
        }
        val scanRecord = scanRecordBuilder.build()
        
        // Use reflection to create ScanResult since constructor is not public
        return createScanResultWithReflection(scanRecord, rssi, timestampNanos)
    }

    private fun createMockScanResultWithDifferentUuid(serviceData: ByteArray?, rssi: Int, timestampNanos: Long): android.bluetooth.le.ScanResult {
        val scanRecordBuilder = android.bluetooth.le.ScanRecord.Builder()
        if (serviceData != null) {
            // Use a different UUID
            val differentUuid = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001")
            scanRecordBuilder.setServiceData(android.os.ParcelUuid(differentUuid), serviceData)
        }
        val scanRecord = scanRecordBuilder.build()
        return createScanResultWithReflection(scanRecord, rssi, timestampNanos)
    }

    private fun createScanResultWithReflection(scanRecord: android.bluetooth.le.ScanRecord, rssi: Int, timestampNanos: Long): android.bluetooth.le.ScanResult {
        // ScanResult constructor: public ScanResult(ScanRecord scanRecord, int rssi, long timestampNanos)
        return android.bluetooth.le.ScanResult(scanRecord, rssi, timestampNanos)
    }
}