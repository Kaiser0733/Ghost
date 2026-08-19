package com.ghost.blelab.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.ghost.blelab.BleLabApplication
import com.ghost.blelab.R
import com.ghost.blelab.ble.scanner.ScanResultProcessor
import com.ghost.blelab.ble.scanner.ScannerBroadcastReceiver
import com.ghost.blelab.ble.scanner.ScannerCallbackHolder
import com.ghost.blelab.ephemeral.EphemeralIdGeneratorImpl
import com.ghost.blelab.ephemeral.TimeSlotCalculatorImpl
import com.ghost.blelab.experiment.ExperimentConfig
import com.ghost.blelab.experiment.Role
import com.ghost.blelab.measurement.DetectionRecord
import com.ghost.blelab.ble.advertiser.BleAdvertiser
import com.ghost.blelab.ble.scanner.BleScanner
import com.ghost.blelab.util.FileUtil
import com.ghost.blelab.util.JsonUtil
import com.ghost.blelab.util.flatMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

/**
 * Foreground service hosting the BLE Feasibility Lab experiment.
 *
 * Phase F of the lab plan. Responsibilities:
 * - Keep the experiment alive in the background via a persistent notification
 *   (foreground service type `connectedDevice`, Android 14+ compatible).
 * - Drive the existing advertiser/scanner lifecycle through the app-level
 *   [com.ghost.blelab.experiment.ExperimentController] singleton.
 * - Rotate the advertiser's ephemeral ID on the existing TimeSlotCalculator
 *   cadence (no second independent timing system).
 * - Record scanner detections through the existing MeasurementRecorder.
 * - Survive process death (START_STICKY + persisted config) and shut down
 *   cleanly on stop, Bluetooth loss, or task removal.
 *
 * This is an EXPERIMENT harness, not Ghost production code. It touches no
 * product module, persists no device/personal identifiers, and performs no
 * networking, location, or Wi-Fi activity.
 */
class BleExperimentService : Service() {

    companion object {
        const val ACTION_START = "com.ghost.blelab.service.ACTION_START"
        const val ACTION_STOP = "com.ghost.blelab.service.ACTION_STOP"
        const val EXTRA_CONFIG_JSON = "com.ghost.blelab.service.EXTRA_CONFIG_JSON"

        private const val TAG = "BleExperimentService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "ble_lab_experiment"
        private const val CONFIG_FILE_NAME = "active_experiment_config.json"

        /**
         * Process-wide running state, maintained by the service itself.
         * [ServiceController.isRunning] reads [serviceRunning]; the UI
         * observes [runningState] so screens recompose when the service
         * starts or stops.
         */
        val runningState = kotlinx.coroutines.flow.MutableStateFlow(false)

        internal var serviceRunning: Boolean
            get() = runningState.value
            set(value) {
                runningState.value = value
            }

        /**
         * Observable start status so the UI can show WHY a start failed.
         * The service runs in its own component; without this channel every
         * failure (Bluetooth off, permission missing, scan registration
         * failed) was only visible in logcat while the UI showed "Stopped"
         * with no explanation.
         */
        sealed class StartStatus {
            /** No start attempted in this process yet. */
            object Idle : StartStatus()

            /** Start requested; waiting for foreground + BLE startup. */
            object Starting : StartStatus()

            /** Experiment running. */
            object Running : StartStatus()

            /** Start failed with an actionable reason. */
            data class Failed(val reason: String) : StartStatus()

            /** Experiment stopped normally. */
            object Stopped : StartStatus()
        }

        val startStatus = kotlinx.coroutines.flow.MutableStateFlow<StartStatus>(StartStatus.Idle)
    }

    private val app: BleLabApplication by lazy { application as BleLabApplication }
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var rotationJob: Job? = null
    private var scanReceiverRegistered = false
    private var bluetoothReceiverRegistered = false
    private val scanResultProcessor = ScanResultProcessor()

    // Advertiser rotation state. The daily key lives only in memory for the
    // lifetime of this run — it is never written to disk.
    private val ephemeralIdGenerator = EphemeralIdGeneratorImpl()
    private var dailyKey: ByteArray? = null
    private var rotationCoordinator: RotationCoordinator? = null

    // -------------------------------------------------------------------------
    // Service lifecycle
    // -------------------------------------------------------------------------

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                Log.i(TAG, "Stop requested via intent")
                stopExperimentAndSelf()
                return START_NOT_STICKY
            }
            ACTION_START -> {
                val configJson = intent.getStringExtra(EXTRA_CONFIG_JSON)
                val config = configJson?.let {
                    JsonUtil.fromJson(ExperimentConfig.serializer(), it).getOrNull()
                }
                if (config == null) {
                    Log.e(TAG, "ACTION_START received without valid config; stopping")
                    startStatus.value = StartStatus.Failed("Start intent carried no valid experiment config")
                    stopExperimentAndSelf()
                    return START_NOT_STICKY
                }
                startStatus.value = StartStatus.Starting
                if (!startForegroundWithNotification(config)) {
                    Log.e(TAG, "Foreground start failed; experiment not started")
                    startStatus.value = StartStatus.Failed(
                        "Could not start the foreground service. Check that notifications are allowed for this app."
                    )
                    stopExperimentAndSelf()
                    return START_NOT_STICKY
                }
                startExperimentInternal(config)
            }
            else -> {
                // Null intent after process death (START_STICKY restart):
                // attempt to resume from persisted config.
                val persisted = loadPersistedConfig()
                if (persisted != null && startForegroundWithNotification(persisted)) {
                    Log.i(TAG, "Resuming experiment after process restart")
                    startStatus.value = StartStatus.Starting
                    startExperimentInternal(persisted)
                } else {
                    Log.i(TAG, "Restarted with no persisted config or foreground failed; stopping")
                    stopExperimentAndSelf()
                    return START_NOT_STICKY
                }
            }
        }
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        // Experiment is designed to survive recents removal; the foreground
        // notification keeps the service alive. Log for post-test analysis.
        Log.i(TAG, "Task removed from recents; experiment continues in foreground")
        super.onTaskRemoved(rootIntent)
    }

    override fun onDestroy() {
        cleanupExperimentResources()
        serviceRunning = false
        serviceScope.cancel()
        Log.i(TAG, "Service destroyed")
        super.onDestroy()
    }

    // -------------------------------------------------------------------------
    // Experiment start/stop
    // -------------------------------------------------------------------------

    private fun startExperimentInternal(config: ExperimentConfig) {
        if (app.experimentController.isRunning()) {
            Log.w(TAG, "ExperimentController already running; not double-starting")
            serviceRunning = true
            startStatus.value = StartStatus.Running
            return
        }

        // Guard: Bluetooth must be available and enabled before we begin.
        bluetoothAvailabilityProblem(config)?.let { problem ->
            Log.e(TAG, "Experiment not started: $problem")
            startStatus.value = StartStatus.Failed(problem)
            stopExperimentAndSelf()
            return
        }

        val result = try {
            app.experimentController.startExperiment(config)
        } catch (e: Exception) {
            // BLE framework calls (startScan/startAdvertising) can throw
            // SecurityException or IllegalStateException on some OEM/Android
            // versions instead of returning an error code. Convert to Result
            // so the failure is reported to the UI instead of crashing.
            Result.failure(e)
        }
        result.fold(
            onSuccess = {
                serviceRunning = true
                startStatus.value = StartStatus.Running
                persistConfig(config)
                registerBluetoothStateReceiver()
                when (config.role) {
                    Role.ADVERTISER -> startRotationLoop(config)
                    Role.SCANNER -> startScanResultRecording()
                    else -> Log.w(TAG, "Experiment started with role ${config.role}; no role-specific service work")
                }
                Log.i(TAG, "Experiment started: role=${config.role}, rotation=${config.rotationIntervalMinutes}min")
            },
            onFailure = { error ->
                Log.e(TAG, "Experiment failed to start: ${error.message}")
                startStatus.value = StartStatus.Failed(
                    "${config.role.name} failed to start: ${error.message ?: error::class.simpleName}"
                )
                stopExperimentAndSelf()
            }
        )
    }

    private fun stopExperimentAndSelf() {
        cleanupExperimentResources()
        if (app.experimentController.isRunning()) {
            app.experimentController.stopExperiment().onFailure {
                Log.e(TAG, "Error stopping experiment controller: ${it.message}")
            }
        }
        deletePersistedConfig()
        serviceRunning = false
        // Never overwrite a Failed status — the UI must keep showing the
        // failure reason. Only a normal stop transitions to Stopped.
        val status = startStatus.value
        if (status is StartStatus.Running || status is StartStatus.Starting) {
            startStatus.value = StartStatus.Stopped
        }
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun cleanupExperimentResources() {
        rotationJob?.cancel()
        rotationJob = null
        dailyKey = null
        rotationCoordinator = null
        if (scanReceiverRegistered) {
            try {
                scanResultsReceiver?.let { unregisterReceiver(it) }
            } catch (e: IllegalArgumentException) {
                // Already unregistered — safe to ignore.
            }
            scanResultsReceiver = null
            scanReceiverRegistered = false
        }
        ScannerCallbackHolder.callbackRef = null
        if (bluetoothReceiverRegistered) {
            try {
                unregisterReceiver(bluetoothStateReceiver)
            } catch (e: IllegalArgumentException) {
                // Already unregistered — safe to ignore.
            }
            bluetoothReceiverRegistered = false
        }
    }

    // -------------------------------------------------------------------------
    // Advertiser rotation (driven by existing TimeSlotCalculator)
    // -------------------------------------------------------------------------

    private fun startRotationLoop(config: ExperimentConfig) {
        val key = ephemeralIdGenerator.generateDailyKey()
        dailyKey = key
        val interval = config.rotationIntervalMinutes
        val coordinator = RotationCoordinator(
            timeSlotCalculator = TimeSlotCalculatorImpl(),
            rotationIntervalMinutes = interval,
            deriveEphemeralId = { slot -> ephemeralIdGenerator.deriveEphemeralId(key, slot) }
        )
        rotationCoordinator = coordinator

        rotationJob = serviceScope.launch {
            val advertiser = app.bleAdvertiser
            while (isActive) {
                when (val action = coordinator.nextAction()) {
                    is RotationCoordinator.Action.Rotate -> {
                        val updateResult = advertiser.updateEphemeralId(action.ephemeralId)
                        updateResult.fold(
                            onSuccess = {
                                coordinator.confirmRotation(action.timeSlot)
                                Log.i(TAG, "Ephemeral ID rotated at time slot ${action.timeSlot}")
                            },
                            onFailure = { error ->
                                // Do not confirm; retry on next loop iteration.
                                Log.e(TAG, "Rotation failed (will retry): ${error.message}")
                                delay(ROTATION_RETRY_DELAY_MS)
                            }
                        )
                    }
                    is RotationCoordinator.Action.Wait -> delay(action.delayMillis)
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Scanner recording (existing PendingIntent scan path -> recorder)
    // -------------------------------------------------------------------------

    private fun startScanResultRecording() {
        ScannerCallbackHolder.callbackRef = object : BleScanner.ScanCallback {
            override fun onScanResult(result: android.bluetooth.le.ScanResult) {
                recordIfExperimentPayload(result)
            }

            override fun onBatchScanResults(results: List<android.bluetooth.le.ScanResult>) {
                results.forEach { recordIfExperimentPayload(it) }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e(TAG, "Scan failed with code $errorCode")
                startStatus.value = StartStatus.Failed(
                    "BLE scan failed (error code $errorCode). Try stopping and starting again."
                )
            }
        }
        scanResultsReceiver = ScannerBroadcastReceiver()
        registerReceiver(
            scanResultsReceiver,
            IntentFilter(ScannerBroadcastReceiver.ACTION_SCAN_RESULTS),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        scanReceiverRegistered = true
    }

    private var scanResultsReceiver: ScannerBroadcastReceiver? = null

    private fun recordIfExperimentPayload(result: android.bluetooth.le.ScanResult) {
        val processed = scanResultProcessor.processScanResult(result)
        if (processed !is ScanResultProcessor.ProcessResult.Valid) return

        val run = app.experimentController.getCurrentRun() ?: return
        val config = app.experimentController.getCurrentConfig() ?: return

        val record = DetectionRecord(
            localTimestamp = System.currentTimeMillis(),
            ephemeralId = processed.ephemeralId,
            rssi = processed.rssi,
            scanResultTimestamp = processed.scanTimestampNanos,
            deviceLocalExperimentId = run.id,
            testCondition = config.testCondition,
            distanceLabelMeters = config.testCondition.distanceMeters
        )
        app.measurementRecorder.recordDetection(record).onFailure {
            Log.e(TAG, "Failed to record detection: ${it.message}")
        }
    }

    // -------------------------------------------------------------------------
    // Bluetooth state handling
    // -------------------------------------------------------------------------

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            if (state == BluetoothAdapter.STATE_OFF || state == BluetoothAdapter.STATE_TURNING_OFF) {
                Log.w(TAG, "Bluetooth turned off during experiment; stopping cleanly")
                stopExperimentAndSelf()
            }
        }
    }

    private fun registerBluetoothStateReceiver() {
        registerReceiver(bluetoothStateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        bluetoothReceiverRegistered = true
    }

    /**
     * Returns null when Bluetooth is usable for the given role, otherwise a
     * human-readable, actionable reason string for the UI.
     */
    private fun bluetoothAvailabilityProblem(config: ExperimentConfig): String? {
        val manager = getSystemService(BluetoothManager::class.java)
            ?: return "Bluetooth system service is unavailable on this device"
        val adapter = manager.adapter
            ?: return "This device has no Bluetooth adapter"
        if (!adapter.isEnabled) {
            return "Bluetooth is turned off. Enable Bluetooth and try again."
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val needed = when (config.role) {
                Role.ADVERTISER -> arrayOf(
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
                Role.SCANNER -> arrayOf(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
                else -> arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
            val missing = needed.filter { checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED }
            if (missing.isNotEmpty()) {
                return "Missing permission(s): ${missing.joinToString(", ") { it.substringAfterLast('.') }}. " +
                    "Grant them in Android settings and try again."
            }
        }
        return null
    }

    // -------------------------------------------------------------------------
    // Foreground notification
    // -------------------------------------------------------------------------

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.ble_lab_notification_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.ble_lab_notification_channel_description)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun startForegroundWithNotification(config: ExperimentConfig): Boolean {
        val notification = buildNotification(config)
        return try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            )
            true
        } catch (e: Exception) {
            // Missing POST_NOTIFICATIONS permission or foreground restriction:
            // log and stop rather than crash.
            Log.e(TAG, "Unable to start foreground: ${e.message}")
            false
        }
    }

    private fun buildNotification(config: ExperimentConfig): Notification {
        val openIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, com.ghost.blelab.ui.MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val stopIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, BleExperimentService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val roleText = when (config.role) {
            Role.ADVERTISER -> getString(R.string.ble_lab_role_advertiser)
            Role.SCANNER -> getString(R.string.ble_lab_role_scanner)
            else -> config.role.name
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.ble_lab_notification_title))
            .setContentText(getString(R.string.ble_lab_notification_text, roleText))
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOngoing(true)
            .setContentIntent(openIntent)
            .addAction(0, getString(R.string.ble_lab_notification_stop), stopIntent)
            .build()
    }

    // -------------------------------------------------------------------------
    // Config persistence (survive process death; no identifiers stored)
    // -------------------------------------------------------------------------

    private fun configFile(): File = File(FileUtil.getFilesDir(this), CONFIG_FILE_NAME)

    private fun persistConfig(config: ExperimentConfig) {
        val json = JsonUtil.toJson(ExperimentConfig.serializer(), config)
        FileUtil.writeJson(configFile(), json).onFailure {
            Log.e(TAG, "Failed to persist experiment config: ${it.message}")
        }
    }

    private fun loadPersistedConfig(): ExperimentConfig? =
        FileUtil.readJson(configFile())
            .flatMap { JsonUtil.fromJson(ExperimentConfig.serializer(), it) }
            .getOrNull()

    private fun deletePersistedConfig() {
        configFile().delete()
    }
}

private const val ROTATION_RETRY_DELAY_MS = 5_000L
