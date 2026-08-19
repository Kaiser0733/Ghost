package com.ghost.blelab.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ghost.blelab.BleLabApplication
import com.ghost.blelab.experiment.ExperimentConfig
import com.ghost.blelab.experiment.Role
import com.ghost.blelab.experiment.TestCondition
import com.ghost.blelab.util.JsonUtil

/**
 * UI-facing controller for the BLE experiment foreground service (Phase F).
 *
 * The UI never touches [BleExperimentService] directly; all start/stop goes
 * through intent actions so the service owns the experiment lifecycle.
 * Test-condition updates are forwarded to the shared
 * [com.ghost.blelab.experiment.ExperimentController] singleton, which the
 * service reads from — so condition changes apply to a running experiment
 * without restarting it.
 */
class ServiceController(private val context: Context) {

    private val appContext: Context = context.applicationContext

    /**
     * Start the experiment inside the foreground service.
     * Fails fast (without starting the service) if the config has no role.
     */
    fun startExperiment(config: ExperimentConfig): Result<Unit> {
        if (config.role == Role.UNSPECIFIED) {
            return Result.failure(IllegalStateException("Cannot start experiment without a role"))
        }
        if (isRunning()) {
            return Result.failure(IllegalStateException("Experiment service already running"))
        }
        // Merge a test condition chosen in the UI before any run existed.
        val app = appContext as? BleLabApplication
        val effectiveConfig = ExperimentStartConfigMerger.merge(
            config = config,
            pendingCondition = app?.pendingTestCondition,
            onConsumed = { app?.pendingTestCondition = null }
        )
        val intent = Intent(appContext, BleExperimentService::class.java).apply {
            action = BleExperimentService.ACTION_START
            putExtra(
                BleExperimentService.EXTRA_CONFIG_JSON,
                JsonUtil.toJson(ExperimentConfig.serializer(), effectiveConfig)
            )
        }
        return try {
            BleExperimentService.startStatus.value = BleExperimentService.StartStatus.Starting
            ContextCompat.startForegroundService(appContext, intent)
            Result.success(Unit)
        } catch (e: Exception) {
            BleExperimentService.startStatus.value = BleExperimentService.StartStatus.Failed(
                "Could not start the experiment service: ${e.message ?: e::class.simpleName}"
            )
            Result.failure(e)
        }
    }

    /**
     * Stop the experiment and tear down the foreground service.
     * No-op success if the service is not running.
     */
    fun stopExperiment(): Result<Unit> {
        if (!isRunning()) {
            return Result.success(Unit)
        }
        val intent = Intent(appContext, BleExperimentService::class.java).apply {
            action = BleExperimentService.ACTION_STOP
        }
        return try {
            appContext.startService(intent)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Whether the experiment foreground service is currently active. */
    fun isRunning(): Boolean = BleExperimentService.serviceRunning

    /**
     * Update the test condition of a running (or future) experiment.
     * While a run is active this delegates to the shared ExperimentController;
     * before any run exists it is stored as a pending condition and merged
     * into the config by [startExperiment].
     */
    fun updateTestCondition(condition: TestCondition) {
        val app = appContext as? BleLabApplication ?: return
        if (app.experimentController.isRunning()) {
            app.experimentController.setTestCondition(condition)
        } else {
            app.pendingTestCondition = condition
        }
    }
}
