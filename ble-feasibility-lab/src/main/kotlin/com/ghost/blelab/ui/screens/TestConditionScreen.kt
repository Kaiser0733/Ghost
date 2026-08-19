package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghost.blelab.ui.components.DropdownField
import com.ghost.blelab.ui.components.LabScrollScreen
import com.ghost.blelab.ui.components.PrimaryButton
import com.ghost.blelab.ui.components.SecondaryButton

@Composable
fun TestConditionScreen(
    experimentController: com.ghost.blelab.experiment.ExperimentController,
    serviceController: com.ghost.blelab.service.ServiceController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val config = experimentController.getCurrentConfig()
    val testCondition = config?.testCondition ?: com.ghost.blelab.experiment.TestCondition.UNSPECIFIED

    var distance by remember { mutableStateOf(testCondition.distanceMeters) }
    var environment by remember { mutableStateOf(testCondition.environment) }
    var deviceState by remember { mutableStateOf(testCondition.deviceState) }
    var orientation by remember { mutableStateOf(testCondition.orientation) }
    var pocketState by remember { mutableStateOf(testCondition.pocketState) }
    var wallCondition by remember { mutableStateOf(testCondition.wallCondition) }

    val distanceOptions = listOf("Not set", "1", "3", "5", "10", "20")
    val environmentOptions = com.ghost.blelab.experiment.Environment.values().map { it.name }
    val deviceStateOptions = com.ghost.blelab.experiment.DeviceState.values().map { it.name }
    val orientationOptions = com.ghost.blelab.experiment.Orientation.values().map { it.name }
    val pocketStateOptions = com.ghost.blelab.experiment.PocketState.values().map { it.name }
    val wallConditionOptions = com.ghost.blelab.experiment.WallCondition.values().map { it.name }

    LabScrollScreen(modifier = modifier) {
        Text(
            text = "Test Condition",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color(0xFF1565C0)
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

        DropdownField(
            label = "Wall Condition",
            value = wallCondition.name,
            options = wallConditionOptions,
            onValueChange = { wallCondition = com.ghost.blelab.experiment.WallCondition.valueOf(it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Apply & Return",
            onClick = {
                val updatedCondition = com.ghost.blelab.experiment.TestCondition(
                    distanceMeters = distance,
                    environment = environment,
                    deviceState = deviceState,
                    orientation = orientation,
                    pocketState = pocketState,
                    wallCondition = wallCondition
                )
                // Routes through ServiceController: applies to a running run,
                // or stores as pending condition merged at next start.
                serviceController.updateTestCondition(updatedCondition)
                onBack()
            }
        )

        SecondaryButton(
            text = "Cancel",
            onClick = onBack
        )
    }
}
