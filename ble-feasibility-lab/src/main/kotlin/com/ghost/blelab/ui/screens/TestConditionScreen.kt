package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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

@Composable
fun TestConditionScreen(
    experimentController: com.ghost.blelab.experiment.ExperimentController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = experimentController.getCurrentConfig()
    val testCondition = config?.testCondition ?: com.ghost.blelab.experiment.TestCondition.UNSPECIFIED

    val distance by remember { mutableStateOf(testCondition.distanceMeters) }
    val environment by remember { mutableStateOf(testCondition.environment) }
    val deviceState by remember { mutableStateOf(testCondition.deviceState) }
    val orientation by remember { mutableStateOf(testCondition.orientation) }
    val pocketState by remember { mutableStateOf(testCondition.pocketState) }

    val distanceOptions = listOf("Not set", "1", "3", "5", "10", "20")
    val environmentOptions = com.ghost.blelab.experiment.Environment.values().map { it.name }
    val deviceStateOptions = com.ghost.blelab.experiment.DeviceState.values().map { it.name }
    val orientationOptions = com.ghost.blelab.experiment.Orientation.values().map { it.name }
    val pocketStateOptions = com.ghost.blelab.experiment.PocketState.values().map { it.name }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Test Condition",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.graphics.Color(0xFF1565C0)
        )

        DropdownField(
            label = "Distance (meters)",
            value = distance?.toString() ?: "Not set",
            options = distanceOptions,
            onValueChange = { distance = if (it == "Not set") null else it.toIntOrNull() }
        )

        DropdownField(
            label = "Environment",
            value = environment.name,
            options = environmentOptions,
            onValueChange = { environment = com.ghost.blelab.experiment.Environment.valueOf(it) }
        )

        DropdownField(
            label = "Device State",
            value = deviceState.name,
            options = deviceStateOptions,
            onValueChange = { deviceState = com.ghost.blelab.experiment.DeviceState.valueOf(it) }
        )

        DropdownField(
            label = "Orientation",
            value = orientation.name,
            options = orientationOptions,
            onValueChange = { orientation = com.ghost.blelab.experiment.Orientation.valueOf(it) }
        )

        DropdownField(
            label = "Pocket State",
            value = pocketState.name,
            options = pocketStateOptions,
            onValueChange = { pocketState = com.ghost.blelab.experiment.PocketState.valueOf(it) }
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        PrimaryButton(
            text = "Apply & Return",
            onClick = {
                val updatedCondition = com.ghost.blelab.experiment.TestCondition(
                    distanceMeters = distance,
                    environment = environment,
                    deviceState = deviceState,
                    orientation = orientation,
                    pocketState = pocketState
                )
                experimentController.setTestCondition(updatedCondition)
                onBack()
            }
        )

        SecondaryButton(
            text = "Cancel",
            onClick = onBack
        )
    }
}