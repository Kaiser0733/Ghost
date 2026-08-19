package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghost.blelab.service.BleExperimentService
import com.ghost.blelab.service.StartStatus
import com.ghost.blelab.ui.components.ErrorBanner
import com.ghost.blelab.ui.components.InfoBanner
import com.ghost.blelab.ui.components.InfoRow
import com.ghost.blelab.ui.components.LabScrollScreen
import com.ghost.blelab.ui.components.PrimaryButton
import com.ghost.blelab.ui.components.SecondaryButton
import com.ghost.blelab.ui.components.SectionTitle
import com.ghost.blelab.ui.components.StatusBadge

@Composable
fun ControlScreen(
    experimentController: com.ghost.blelab.experiment.ExperimentController,
    serviceController: com.ghost.blelab.service.ServiceController,
    bleAdvertiser: com.ghost.blelab.ble.advertiser.BleAdvertiser,
    bleScanner: com.ghost.blelab.ble.scanner.BleScanner,
    measurementRecorder: com.ghost.blelab.measurement.MeasurementRecorder,
    experimentExporter: com.ghost.blelab.export.ExperimentExporter,
    context: android.content.Context,
    onNavigateToResults: () -> Unit,
    onNavigateToTestCondition: () -> Unit,
    lastSelectedRole: com.ghost.blelab.experiment.Role? = null,
    initialError: String? = null,
    modifier: Modifier = Modifier
) {
    // Observe the foreground service so this screen recomposes when the
    // experiment starts/stops (including stops from the notification).
    val serviceRunning by BleExperimentService.runningState.collectAsState()
    // Observe the start status so service-side failures (Bluetooth off,
    // missing permission, scan registration failed) are shown here instead
    // of disappearing into logcat.
    val startStatus by BleExperimentService.startStatus.collectAsState()

    val config = experimentController.getCurrentConfig()
    val isRunning = experimentController.isRunning() || serviceRunning
    val isAdvertising = bleAdvertiser.isAdvertising()
    val isScanning = bleScanner.isScanning()

    val rotationInterval by remember { mutableStateOf(config?.rotationIntervalMinutes ?: 10) }
    val txPower by remember { mutableStateOf(config?.txPowerLevel ?: android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM) }

    var actionError by remember { mutableStateOf(initialError) }
    var exportMessage by remember { mutableStateOf<String?>(null) }

    // Service-side failure reason, if the last start attempt failed.
    val serviceFailure = (startStatus as? StartStatus.Failed)?.reason
    val isStarting = startStatus is StartStatus.Starting

    val statusMessage = when {
        isRunning && isAdvertising -> "Advertising"
        isRunning && isScanning -> "Scanning"
        isRunning -> "Running"
        isStarting -> "Starting..."
        else -> "Stopped"
    }

    LabScrollScreen(modifier = modifier) {
        // Status
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusBadge(text = statusMessage, isActive = isRunning)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = config?.role?.name ?: lastSelectedRole?.name ?: "No role selected",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.87f)
            )
        }

        // Errors / feedback surfaced inline instead of failing silently
        actionError?.let { ErrorBanner(message = it) }
        serviceFailure?.let { ErrorBanner(message = it) }
        exportMessage?.let { InfoBanner(message = it) }

        Spacer(modifier = Modifier.height(8.dp))

        // Configuration
        SectionTitle("Configuration")

        InfoRow("Rotation Interval", "${rotationInterval} min")
        InfoRow("TX Power", when (txPower) {
            android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW -> "Ultra Low"
            android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_LOW -> "Low"
            android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM -> "Medium"
            android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_HIGH -> "High"
            else -> "Unknown"
        })
        InfoRow("Scan Mode", config?.scanMode?.name ?: "LOW_POWER")
        InfoRow("Advertise Mode", config?.advertisingMode?.name ?: "LOW_POWER")

        Spacer(modifier = Modifier.height(16.dp))

        // Test Condition
        SectionTitle("Test Condition")
        val testCondition = config?.testCondition ?: com.ghost.blelab.experiment.TestCondition.UNSPECIFIED
        InfoRow("Distance", testCondition.distanceMeters?.let { "${it}m" } ?: "Not set")
        InfoRow("Environment", testCondition.environment.name)
        InfoRow("Device State", testCondition.deviceState.name)
        InfoRow("Orientation", testCondition.orientation.name)
        InfoRow("Pocket State", testCondition.pocketState.name)
        InfoRow("Wall Condition", testCondition.wallCondition.name)

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        PrimaryButton(
            text = if (isRunning) "Stop Experiment" else "Start Experiment",
            onClick = {
                actionError = null
                exportMessage = null
                // Clear any stale service failure from a previous attempt.
                BleExperimentService.startStatus.value = StartStatus.Idle
                if (isRunning) {
                    serviceController.stopExperiment().onFailure {
                        actionError = "Failed to stop experiment: ${it.message}"
                    }
                } else {
                    val role = config?.role?.takeIf { it != com.ghost.blelab.experiment.Role.UNSPECIFIED }
                        ?: lastSelectedRole
                    if (role == null) {
                        actionError = "No role selected. Go back and choose Advertiser or Scanner first."
                    } else {
                        val updatedConfig = (config ?: com.ghost.blelab.experiment.ExperimentConfig(role = role)).copy(
                            role = role,
                            rotationIntervalMinutes = rotationInterval,
                            txPowerLevel = txPower
                        )
                        serviceController.startExperiment(updatedConfig).onFailure {
                            actionError = "Failed to start experiment: ${it.message}"
                        }
                    }
                }
            }
        )

        SecondaryButton(
            text = "Set Test Condition",
            onClick = onNavigateToTestCondition
        )

        SecondaryButton(
            text = "View Results",
            onClick = onNavigateToResults
        )

        SecondaryButton(
            text = "Export Data (Scanner only)",
            onClick = {
                actionError = null
                exportMessage = null
                if (config?.role != com.ghost.blelab.experiment.Role.SCANNER) {
                    actionError = "Export is only available for the Scanner role."
                    return@SecondaryButton
                }
                val run = experimentController.getCurrentRun()
                if (run == null) {
                    actionError = "No experiment run to export yet."
                } else {
                    experimentExporter.exportExperiment(
                        run.id,
                        com.ghost.blelab.export.ExportFormat.CSV,
                        context
                    ).fold(
                        onSuccess = { result ->
                            exportMessage = "Export complete: ${result.recordCount} records (${result.fileSizeBytes} bytes)"
                        },
                        onFailure = {
                            actionError = "Export failed: ${it.message}"
                        }
                    )
                }
            }
        )

        // Capability info
        if (!isRunning) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("Device Capability")
            InfoRow("BLE Advertising", if (bleAdvertiser.isAdvertising()) "Active" else "Available")
            InfoRow("BLE Scanning", if (bleScanner.isScanning()) "Active" else "Available")
        }
    }
}
