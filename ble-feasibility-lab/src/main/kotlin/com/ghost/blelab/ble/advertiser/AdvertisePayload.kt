package com.ghost.blelab.ble.advertiser

import com.ghost.blelab.ble.common.BleConstants
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets

/**
 * Advertisement payload for BLE Feasibility Lab experiment.
 * 
 * Payload Layout (Service Data):
 * ```
 * Byte 0:        Protocol version (1 byte) = 1
 * Bytes 1-16:    Ephemeral ID (16 bytes)
 * Total:         17 bytes
 * ```
 * 
 * This is advertised in the Service Data field with the experiment service UUID.
 * The service UUID itself acts as the experiment identifier.
 * 
 * No device information, MAC addresses, GPS coordinates, or personal identifiers
 * are included in the payload.
 */
@Serializable
data class AdvertisePayload(
    val protocolVersion: Byte = BleConstants.PROTOCOL_VERSION,
    val ephemeralId: ByteArray
) {
    /**
     * Serialize to service data bytes for BLE advertisement.
     * Format: [protocolVersion:1][ephemeralId:16]
     */
    fun toServiceData(): ByteArray {
        require(ephemeralId.size == BleConstants.EPHEMERAL_ID_LENGTH) {
            "Ephemeral ID must be ${BleConstants.EPHEMERAL_ID_LENGTH} bytes"
        }
        val data = ByteArray(1 + BleConstants.EPHEMERAL_ID_LENGTH)
        data[0] = protocolVersion
        data.copyFrom(ephemeralId, 1, 0, ephemeralId.size)
        return data
    }

    /**
     * Deserialize from service data bytes.
     * Returns null if the data is malformed or doesn't match expected format.
     */
    companion object {
        fun fromServiceData(data: ByteArray): AdvertisePayload? {
            if (data.size != 1 + BleConstants.EPHEMERAL_ID_LENGTH) {
                return null
            }
            val protocolVersion = data[0]
            if (protocolVersion != BleConstants.PROTOCOL_VERSION) {
                return null
            }
            val ephemeralId = data.copyOfRange(1, 1 + BleConstants.EPHEMERAL_ID_LENGTH)
            return AdvertisePayload(protocolVersion = protocolVersion, ephemeralId = ephemeralId)
        }
    }
}