package com.ghost.blelab.ble.common

import java.util.UUID

object BleConstants {
    /**
     * Dedicated 128-bit experimental service UUID for BLE Feasibility Lab.
     * Generated specifically for this experiment. Not a standard Bluetooth SIG UUID.
     * This UUID allows filtering advertisement packets to only our experiment.
     */
    val EXPERIMENT_SERVICE_UUID: UUID = UUID.fromString("d4e5f6a7-b8c9-4d0e-8f1a-2b3c4d5e6f70")

    /** Protocol version for this experiment payload format. */
    const val PROTOCOL_VERSION: Byte = 1

    /** Length of ephemeral identifier in bytes (16 bytes = 128 bits). */
    const val EPHEMERAL_ID_LENGTH = 16

    /** Default rotation interval in minutes (matches GAEN specification). */
    const val DEFAULT_ROTATION_INTERVAL_MINUTES = 10

    /** Minimum rotation interval in minutes. */
    const val MIN_ROTATION_INTERVAL_MINUTES = 1

    /** Maximum rotation interval in minutes. */
    const val MAX_ROTATION_INTERVAL_MINUTES = 60

    /** Default TX power level for advertising. */
    const val DEFAULT_TX_POWER_LEVEL = android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM
}