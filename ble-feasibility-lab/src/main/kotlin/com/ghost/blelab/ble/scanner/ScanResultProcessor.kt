package com.ghost.blelab.ble.scanner

import android.bluetooth.le.ScanResult
import com.ghost.blelab.ble.advertiser.AdvertisePayload
import com.ghost.blelab.ble.common.BleConstants
import android.os.ParcelUuid
import java.util.Locale

/**
 * Processes BLE scan results to extract experimental advertisement data.
 * 
 * This class is pure logic - no Android radio dependencies.
 * Unit-testable for payload parsing and validation.
 */
class ScanResultProcessor {

    /**
     * Result of processing a scan result for our experiment.
     */
    sealed class ProcessResult {
        data class Valid(
            val ephemeralId: ByteArray,
            val protocolVersion: Byte,
            val rssi: Int,
            val scanTimestampNanos: Long,
            val rawServiceData: ByteArray
        ) : ProcessResult()
        
        data class Invalid(val reason: String) : ProcessResult()
    }

    /**
     * Process a scan result to extract experiment data.
     * Returns Valid with extracted data or Invalid with reason.
     */
    fun processScanResult(scanResult: ScanResult): ProcessResult {
        // Check if scan result has service data
        val serviceDataMap = scanResult.scanRecord?.serviceData
        if (serviceDataMap == null || serviceDataMap.isEmpty()) {
            return ProcessResult.Invalid("No service data in scan result")
        }

        // Find our experiment service UUID
        val parcelUuid = ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID)
        val serviceData = serviceDataMap[parcelUuid]
        if (serviceData == null) {
            return ProcessResult.Invalid("Experiment service UUID not found in service data")
        }

        // Parse the service data using AdvertisePayload parser
        val payload = AdvertisePayload.fromServiceData(serviceData)
        if (payload == null) {
            val hex = serviceData.joinToString("") { String.format(Locale.US, "%02X", it) }
            return ProcessResult.Invalid("Failed to parse service data: $hex")
        }

        // Validate protocol version
        if (payload.protocolVersion != BleConstants.PROTOCOL_VERSION) {
            return ProcessResult.Invalid("Unsupported protocol version: ${payload.protocolVersion}")
        }

        return ProcessResult.Valid(
            ephemeralId = payload.ephemeralId,
            protocolVersion = payload.protocolVersion,
            rssi = scanResult.rssi,
            scanTimestampNanos = scanResult.timestampNanos,
            rawServiceData = serviceData
        )
    }

    /**
     * Check if a scan result contains our experiment's service UUID.
     * Fast pre-filter before full processing.
     */
    fun hasExperimentServiceData(scanResult: ScanResult): Boolean {
        val serviceDataMap = scanResult.scanRecord?.serviceData
        if (serviceDataMap == null || serviceDataMap.isEmpty()) {
            return false
        }
        val parcelUuid = ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID)
        return serviceDataMap.containsKey(parcelUuid)
    }
}