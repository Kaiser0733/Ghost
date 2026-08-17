package com.ghost.blelab.ble.scanner

import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.ghost.blelab.ble.common.BleConstants
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.Result

/**
 * BLE Scanner implementation for the Feasibility Lab experiment.
 * 
 * Uses BluetoothLeScanner with PendingIntent for background scanning.
 * Handles all BLE lifecycle states gracefully without crashing.
 * 
 * Requirements:
 * - Android 12+ (API 31+) for BLUETOOTH_SCAN permission
 * - BLE central role support
 * - Foreground service with connectedDevice type for background operation
 * - PendingIntent-based scanning for background delivery
 */
class BleScannerImpl(
    private val context: Context,
    private val bluetoothAdapter: BluetoothAdapter,
) : BleScanner {

    private val bluetoothLeScanner: BluetoothLeScanner? = bluetoothAdapter.bluetoothLeScanner
    private val isScanningRef = AtomicBoolean(false)
    private val currentPendingIntentRef = AtomicReference<PendingIntent?>(null)
    private var callbackRef: BleScanner.ScanCallback? = null
    private val scanResultProcessor = ScanResultProcessor()

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (callbackType == ScanSettings.CALLBACK_TYPE_FIRST_MATCH ||
                callbackType == ScanSettings.CALLBACK_TYPE_ALL_MATCHES) {
                callbackRef?.onScanResult(result)
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            if (results.isNotEmpty()) {
                callbackRef?.onBatchScanResults(results)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            val errorMsg = scanErrorToString(errorCode)
            Log.e("BleScanner", "Scan failed: $errorMsg (code: $errorCode)")
            callbackRef?.onScanFailed(errorCode)
        }
    }

    override fun startScanning(
        filters: List<ScanFilter>,
        settings: ScanSettings,
        pendingIntent: PendingIntent
    ): Result<Unit> {
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

        // Validate scanner exists
        val scanner = bluetoothLeScanner ?: return Result.failure(
            IllegalStateException("BluetoothLeScanner not available")
        )

        currentPendingIntentRef.set(pendingIntent)
        isScanningRef.set(true)

        // Start scanning with PendingIntent for background delivery
        scanner.startScan(filters, settings, pendingIntent)

        Log.d("BleScanner", "Scanning started with PendingIntent")
        return Result.success(Unit)
    }

    override fun stopScanning(pendingIntent: PendingIntent): Result<Unit> {
        val scanner = bluetoothLeScanner ?: return Result.failure(
            IllegalStateException("BluetoothLeScanner not available")
        )

        if (!isScanningRef.getAndSet(false)) {
            // Not currently scanning
            return Result.success(Unit)
        }

        currentPendingIntentRef.set(null)
        scanner.stopScan(pendingIntent)

        Log.d("BleScanner", "Scanning stopped")
        return Result.success(Unit)
    }

    override fun setCallback(callback: BleScanner.ScanCallback?) {
        callbackRef = callback
    }

    override fun isScanning(): Boolean = isScanningRef.get()

    /**
     * Validate BLE is available and scanning is supported.
     */
    private fun validateBleAvailable(): Result<Unit> {
        if (bluetoothLeScanner == null) {
            return Result.failure(IllegalStateException("Bluetooth LE scanning not supported on this device"))
        }

        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled) {
            return Result.failure(IllegalStateException("Bluetooth is disabled"))
        }

        // Check if BLE central is supported
        val pm = context.packageManager
        if (!pm.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return Result.failure(IllegalStateException("Bluetooth LE not supported on this device"))
        }

        return Result.success(Unit)
    }

    /**
     * Validate required permissions for scanning.
     */
    private fun validatePermissions(): Result<Unit> {
        // Check BLUETOOTH_SCAN permission (Android 12+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permission = android.Manifest.permission.BLUETOOTH_SCAN
            if (context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                return Result.failure(SecurityException("BLUETOOTH_SCAN permission not granted"))
            }
        }

        return Result.success(Unit)
    }

    /**
     * Convert scan error code to human-readable string.
     */
    private fun scanErrorToString(errorCode: Int): String = when (errorCode) {
        ScanCallback.SCAN_FAILED_ALREADY_STARTED -> "ALREADY_STARTED"
        ScanCallback.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "APPLICATION_REGISTRATION_FAILED"
        ScanCallback.SCAN_FAILED_INTERNAL_ERROR -> "INTERNAL_ERROR"
        ScanCallback.SCAN_FAILED_FEATURE_UNSUPPORTED -> "FEATURE_UNSUPPORTED"
        ScanCallback.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES -> "OUT_OF_HARDWARE_RESOURCES"
        else -> "UNKNOWN_ERROR($errorCode)"
    }

    /**
     * Create scan filter for the experiment service UUID.
     * This filters at the controller level to reduce wake-ups.
     */
    companion object {
        fun createExperimentScanFilter(): ScanFilter {
            return ScanFilter.Builder()
                .setServiceUuid(android.os.ParcelUuid(BleConstants.EXPERIMENT_SERVICE_UUID))
                .build()
        }

        /**
         * Create scan settings for low-power background scanning.
         * Uses auto-batching for screen-off operation.
         */
        fun createLowPowerScanSettings(): ScanSettings {
            return ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES_AUTO_BATCH)
                .setMatchMode(ScanSettings.MATCH_MODE_STICKY)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .setLegacy(true)
                .setPhy(ScanSettings.PHY_LE_ALL_SUPPORTED)
                .build()
        }
    }

    /**
     * Process a scan result using the ScanResultProcessor.
     * Returns the process result for the callback to handle.
     */
    fun processScanResult(scanResult: ScanResult): ScanResultProcessor.ProcessResult {
        return scanResultProcessor.processScanResult(scanResult)
    }
}