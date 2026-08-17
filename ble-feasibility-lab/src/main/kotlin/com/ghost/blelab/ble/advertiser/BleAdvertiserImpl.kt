package com.ghost.blelab.ble.advertiser

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseCallback
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.BluetoothLeAdvertiser
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.ParcelUuid
import android.util.Log
import com.ghost.blelab.ble.advertiser.BleAdvertiser.AdvertisingCallback
import com.ghost.blelab.ble.advertiser.AdvertisePayload
import com.ghost.blelab.ble.common.BleConstants
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.Result

/**
 * BLE Advertiser implementation for the Feasibility Lab experiment.
 * 
 * Uses BluetoothLeAdvertiser with service data containing the experiment payload.
 * Handles all BLE lifecycle states gracefully without crashing.
 * 
 * Requirements:
 * - Android 12+ (API 31+) for BLUETOOTH_ADVERTISE permission
 * - BLE peripheral role support
 * - Foreground service with connectedDevice type for background operation
 */
class BleAdvertiserImpl(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
) : BleAdvertiser {

    private val bluetoothLeAdvertiser: BluetoothLeAdvertiser? = bluetoothAdapter.bluetoothLeAdvertiser
    private val isAdvertisingRef = AtomicBoolean(false)
    private val currentEphemeralIdRef = AtomicReference<ByteArray?>(null)
    private var currentTxPowerLevel = BleConstants.DEFAULT_TX_POWER_LEVEL
    private var callbackRef: AdvertisingCallback? = null

    private val advertiseCallback = object : AdvertiseCallback() {
        override fun onStartSuccess(settingsInEffect: AdvertiseSettings) {
            isAdvertisingRef.set(true)
            callbackRef?.let { it.AdvertisingCallback.OnStartSuccess(settingsInEffect) }
            Log.d("BleAdvertiser", "Advertising started successfully with settings: $settingsInEffect")
        }

        override fun onStartFailure(errorCode: Int) {
            isAdvertisingRef.set(false)
            val errorMsg = advertiseErrorToString(errorCode)
            Log.e("BleAdvertiser", "Advertising failed to start: $errorMsg (code: $errorCode)")
            callbackRef?.let { it.AdvertisingCallback.OnStartFailure(errorCode) }
        }
    }

    override fun startAdvertising(ephemeralId: ByteArray, txPowerLevel: Int): Result<Unit> {
        // Validate BLE availability
        val validationResult = validateBleAvailable()
        if (validationResult.isFailure) {
            return validationResult
        }

        // Validate permissions
        val permissionResult = validatePermissions()
        if (permissionResult.isFailure) {
            return permissionResult
        }

        // Validate ephemeral ID
        if (ephemeralId.size != BleConstants.EPHEMERAL_ID_LENGTH) {
            return Result.failure(IllegalArgumentException(
                "Ephemeral ID must be ${BleConstants.EPHEMERAL_ID_LENGTH} bytes, got ${ephemeralId.size}"
            ))
        }

        // Validate advertiser exists
        val advertiser = bluetoothLeAdvertiser ?: return Result.failure(
            IllegalStateException("BluetoothLeAdvertiser not available")
        )

        currentTxPowerLevel = txPowerLevel
        currentEphemeralIdRef.set(ephemeralId)

        // Build advertise settings
        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
            .setTxPowerLevel(txPowerLevel)
            .setConnectable(false)
            .setTimeout(0) // No timeout - advertise indefinitely
            .build()

        // Build advertise data with service data
        val payload = AdvertisePayload(ephemeralId = ephemeralId)
        val serviceData = payload.toServiceData()

        val data = AdvertiseData.Builder()
            .setIncludeDeviceName(false) // No device name per privacy requirements
            .addServiceData(ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID), serviceData)
            .build()

        // Start advertising
        advertiser.startAdvertising(settings, data, advertiseCallback)

        return Result.success(Unit)
    }

    override fun stopAdvertising(): Result<Unit> {
        val advertiser = bluetoothLeAdvertiser ?: return Result.failure(
            IllegalStateException("BluetoothLeAdvertiser not available")
        )

        if (!isAdvertisingRef.getAndSet(false)) {
            // Not currently advertising
            return Result.success(Unit)
        }

        currentEphemeralIdRef.set(null)
        advertiser.stopAdvertising(advertiseCallback)

        callbackRef?.let { it.AdvertisingCallback.OnStopSuccess }
        Log.d("BleAdvertiser", "Advertising stopped")
        return Result.success(Unit)
    }

    override fun updateEphemeralId(newEphemeralId: ByteArray): Result<Unit> {
        if (newEphemeralId.size != BleConstants.EPHEMERAL_ID_LENGTH) {
            return Result.failure(IllegalArgumentException(
                "Ephemeral ID must be ${BleConstants.EPHEMERAL_ID_LENGTH} bytes"
            ))
        }

        currentEphemeralIdRef.set(newEphemeralId)

        // If currently advertising, restart with new ID
        if (isAdvertisingRef.get()) {
            // Stop first
            val advertiser = bluetoothLeAdvertiser ?: return Result.failure(
                IllegalStateException("BluetoothLeAdvertiser not available")
            )
            advertiser.stopAdvertising(advertiseCallback)

            // Build new data and restart
            val settings = AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setTxPowerLevel(currentTxPowerLevel)
                .setConnectable(false)
                .setTimeout(0)
                .build()

            val payload = AdvertisePayload(ephemeralId = newEphemeralId)
            val serviceData = payload.toServiceData()

            val data = AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .addServiceData(ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID), serviceData)
                .build()

            advertiser.startAdvertising(settings, data, advertiseCallback)
        }

        callbackRef?.let { it.AdvertisingCallback.OnRotationUpdated(newEphemeralId) }
        Log.d("BleAdvertiser", "Ephemeral ID rotated")
        return Result.success(Unit)
    }

    override fun setTxPowerLevel(txPowerLevel: Int) {
        currentTxPowerLevel = txPowerLevel
    }

    override fun isAdvertising(): Boolean = isAdvertisingRef.get()

    override fun setCallback(callback: AdvertisingCallback?) {
        callbackRef = callback
    }

    /**
     * Validate BLE is available and advertising is supported.
     */
    private fun validateBleAvailable(): Result<Unit> {
        if (bluetoothLeAdvertiser == null) {
            return Result.failure(IllegalStateException("Bluetooth LE advertising not supported on this device"))
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            return Result.failure(IllegalStateException("Bluetooth is disabled"))
        }

        // Check if BLE peripheral is supported
        val pm = context.packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return Result.failure(IllegalStateException("Bluetooth LE not supported on this device"))
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Use string constant since FEATURE_BLUETOOTH_LE_PERIPHERAL may not be available in all SDKs
            if (!pm.hasSystemFeature("android.hardware.bluetooth_le.peripheral")) {
                // Peripheral role might not be explicitly declared but could still work
                Log.w("BleAdvertiser", "FEATURE_BLUETOOTH_LE_PERIPHERAL not declared, advertising may not work")
            }
        }

        return Result.success(Unit)
    }

    /**
     * Validate required permissions for advertising.
     */
    private fun validatePermissions(): Result<Unit> {
        // Check BLUETOOTH_ADVERTISE permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = android.Manifest.permission.BLUETOOTH_ADVERTISE
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(SecurityException("BLUETOOTH_ADVERTISE permission not granted"))
            }
        }

        return Result.success(Unit)
    }

    /**
     * Convert advertise error code to human-readable string.
     */
    private fun advertiseErrorToString(errorCode: Int): String = when (errorCode) {
        AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE -> "DATA_TOO_LARGE"
        AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS -> "TOO_MANY_ADVERTISERS"
        AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED -> "ALREADY_STARTED"
        AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR -> "INTERNAL_ERROR"
        AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED -> "FEATURE_UNSUPPORTED"
        else -> "UNKNOWN_ERROR($errorCode)"
    }
}