package com.ghost.blelab.experiment

import com.ghost.blelab.measurement.MeasurementRecorder
import com.ghost.blelab.measurement.MeasurementRecorderImpl
import com.ghost.blelab.util.JsonUtil
import com.ghost.blelab.util.FileUtil
import java.io.File
import java.util.UUID
import kotlin.Result

class ExperimentControllerImpl(
    private val context: android.content.Context,
    private val bleAdvertiser: com.ghost.blelab.ble.advertiser.BleAdvertiser,
    private val bleScanner: com.ghost.blelab.ble.scanner.BleScanner,
    private val measurementRecorder: MeasurementRecorder,
) : ExperimentController {

    private var currentRun: ExperimentRun? = null
    private var currentConfig: ExperimentConfig? = null
    private val runsDir: File by lazy {
        val dir = File(FileUtil.getFilesDir(context), "runs")
        FileUtil.ensureDir(dir).getOrThrow()
        dir
    }

    override fun startExperiment(config: ExperimentConfig): Result<Unit> {
        if (isRunning()) {
            return Result.failure(IllegalStateException("Experiment already running"))
        }

        val runId = UUID.randomUUID().toString()
        val startTime = System.currentTimeMillis()
        val batteryStart = getBatteryLevel()

        currentConfig = config
        currentRun = ExperimentRun(
            id = runId,
            config = config,
            startTime = startTime,
            batteryStartPercent = batteryStart
        )

        // Persist run
        persistRun(currentRun!!)

        // Start BLE based on role
        return when (config.role) {
            com.ghost.blelab.experiment.Role.ADVERTISER -> startAdvertising(config)
            com.ghost.blelab.experiment.Role.SCANNER -> startScanning(config)
            else -> Result.failure(IllegalStateException("Invalid role"))
        }
    }

    override fun stopExperiment(): Result<Unit> {
        if (!isRunning()) {
            return Result.success(Unit)
        }

        val result = when (currentConfig?.role) {
            com.ghost.blelab.experiment.Role.ADVERTISER -> bleAdvertiser.stopAdvertising()
            com.ghost.blelab.experiment.Role.SCANNER -> {
                val pi = com.ghost.blelab.ble.scanner.BleScannerImpl.createExperimentPendingIntent(
                    applicationContext = context
                )
                bleScanner.stopScanning(pi)
            }
            else -> Result.success(Unit)
        }

        // Update run with end time and battery
        currentRun?.let { run ->
            val updatedRun = run.copy(
                endTime = System.currentTimeMillis(),
                batteryEndPercent = getBatteryLevel()
            )
            currentRun = updatedRun
            persistRun(updatedRun)
        }

        currentConfig = null
        return result
    }

    override fun getCurrentConfig(): ExperimentConfig? = currentConfig

    override fun isRunning(): Boolean = currentConfig != null

    override fun setTestCondition(condition: TestCondition) {
        currentConfig?.let { config ->
            currentConfig = config.copy(testCondition = condition)
            currentRun?.let { run ->
                val updatedRun = run.copy(config = currentConfig!!)
                currentRun = updatedRun
                persistRun(updatedRun)
            }
        }
    }

    override fun getCurrentRun(): ExperimentRun? = currentRun

    private fun startAdvertising(config: ExperimentConfig): Result<Unit> {
        // Generate initial ephemeral ID
        val ephemeralId = com.ghost.blelab.ephemeral.EphemeralIdGeneratorImpl()
            .getCurrentEphemeralId(
                com.ghost.blelab.ephemeral.EphemeralIdGeneratorImpl().generateDailyKey(),
                config.rotationIntervalMinutes
            )

        return bleAdvertiser.startAdvertising(ephemeralId, config.txPowerLevel)
    }

    private fun startScanning(config: ExperimentConfig): Result<Unit> {
        val filter = com.ghost.blelab.ble.scanner.BleScannerImpl.createExperimentScanFilter()
        val settings = com.ghost.blelab.ble.scanner.BleScannerImpl.createLowPowerScanSettings()
        val pi = com.ghost.blelab.ble.scanner.BleScannerImpl.createExperimentPendingIntent(
            applicationContext = context
        )

        return bleScanner.startScanning(listOf(filter), settings, pi)
    }

    private fun persistRun(run: ExperimentRun) {
        val runFile = File(runsDir, "${run.id}.json")
        val jsonString = JsonUtil.toJson(ExperimentRun.serializer(), run)
        FileUtil.writeJson(runFile, jsonString).getOrElse { throw it }
    }

    private fun getBatteryLevel(): Int {
        val batteryManager = context.getSystemService(android.content.Context.BATTERY_SERVICE) as android.os.BatteryManager
        return batteryManager.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private val applicationContext: android.content.Context = context.applicationContext
}