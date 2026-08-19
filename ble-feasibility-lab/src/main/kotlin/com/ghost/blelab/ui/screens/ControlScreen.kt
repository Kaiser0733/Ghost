package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghost.blelab.ui.components.InfoRow
import com.ghost.blelab.ui.components.PrimaryButton
import com.ghost.blelab.ui.components.SecondaryButton
import com.ghost.blelab.ui.components.SectionTitle
import com.ghost.blelab.ui.components.StatusBadge

@Composable
fun ControlScreen(
    experimentController: com.ghost.blelab.experiment.ExperimentController,
    bleAdvertiser: com.ghost.blelab.ble.advertiser.BleAdvertiser,
    bleScanner: com.ghost.blelab.ble.scanner.BleScanner,
    measurementRecorder: com.ghost.blelab.measurement.MeasurementRecorder,
    experimentExporter: com.ghost.blelab.export.ExperimentExporter,
    context: android.content.Context,
    onNavigateToResults: () -> Unit,
    onNavigateToTestCondition: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = experimentController.getCurrentConfig()
    val isRunning = experimentController.isRunning()
    val isAdvertising = bleAdvertiser.isAdvertising()
    val isScanning = bleScanner.isScanning()

    val rotationInterval by remember { mutableStateOf(config?.rotationIntervalMinutes ?: 10) }
    val txPower by remember { mutableStateOf(config?.txPowerLevel ?: android.bluetooth.le.AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM) }

    val statusMessage = when {
        isRunning && isAdvertising -> "Advertising"
        isRunning && isScanning -> "Scanning"
        isRunning -> "Starting..."
        else -> "Stopped"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatusBadge(text = statusMessage, isActive = isRunning)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(
                text = config?.role?.name ?: "No role selected",
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.87f)
            )
        }

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

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

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        // Test Condition
        SectionTitle("Test Condition")
        val testCondition = config?.testCondition ?: com.ghost.blelab.experiment.TestCondition.UNSPECIFIED
        InfoRow("Distance", testCondition.distanceMeters?.let { "${it}m" } ?: "Not set")
        InfoRow("Environment", testCondition.environment.name)
        InfoRow("Device State", testCondition.deviceState.name)
        InfoRow("Orientation", testCondition.orientation.name)
        InfoRow("Pocket State", testCondition.pocketState.name)

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

        // Actions
        PrimaryButton(
            text = if (isRunning) "Stop Experiment" else "Start Experiment",
            onClick = {
                if (isRunning) {
                    experimentController.stopExperiment()
                } else {
                    config?.copy(
                        rotationIntervalMinutes = rotationInterval,
                        txPowerLevel = txPower
                    )?.let { updatedConfig ->
                        experimentController.startExperiment(updatedConfig)
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
                if (config?.role == com.ghost.blelab.experiment.Role.SCANNER) {
                    val run = experimentController.getCurrentRun()
                    run?.let {
                        experimentExporter.exportExperiment(it.id, com.ghost.blelab.export.ExportFormat.CSV, context)
                    }
                }
            }
        )

        // Capability info
        if (!isRunning) {
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            SectionTitle("Device Capability")
            InfoRow("BLE Advertising", if (bleAdvertiser.isAdvertising()) "Active" else "Available")
            InfoRow("BLE Scanning", if (bleScanner.isScanning()) "Active" else "Available")
        }
    }
}