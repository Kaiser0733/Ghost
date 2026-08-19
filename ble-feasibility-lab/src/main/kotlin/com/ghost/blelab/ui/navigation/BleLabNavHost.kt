package com.ghost.blelab.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghost.blelab.experiment.ExperimentController
import com.ghost.blelab.experiment.ExperimentConfig
import com.ghost.blelab.experiment.Role
import com.ghost.blelab.ui.screens.ControlScreen
import com.ghost.blelab.ui.screens.RoleScreen
import com.ghost.blelab.ui.screens.TestConditionScreen
import com.ghost.blelab.ui.screens.ResultsScreen

@Composable
fun BleLabNavHost(
    experimentController: ExperimentController,
    bleAdvertiser: com.ghost.blelab.ble.advertiser.BleAdvertiser,
    bleScanner: com.ghost.blelab.ble.scanner.BleScanner,
    measurementRecorder: com.ghost.blelab.measurement.MeasurementRecorder,
    experimentExporter: com.ghost.blelab.export.ExperimentExporter,
    context: android.content.Context
) {
    val navController = rememberNavController()
    val serviceController = remember { com.ghost.blelab.service.ServiceController(context) }

    // Role chosen on the Role screen; kept so the Control screen can show it
    // and restart the experiment after a stop, even before a run exists.
    var selectedRole by remember { mutableStateOf<Role?>(null) }
    // Start failures are surfaced on the Control screen instead of crashing.
    var startError by remember { mutableStateOf<String?>(null) }

    NavHost(navController, startDestination = "role") {
        composable("role") {
            RoleScreen(onRoleSelected = { role ->
                selectedRole = role
                startError = null
                val config = ExperimentConfig(role = role)
                serviceController.startExperiment(config).onFailure { error ->
                    startError = "Failed to start experiment: ${error.message}"
                }
                navController.navigate("control")
            })
        }
        composable("control") {
            ControlScreen(
                experimentController = experimentController,
                serviceController = serviceController,
                bleAdvertiser = bleAdvertiser,
                bleScanner = bleScanner,
                measurementRecorder = measurementRecorder,
                experimentExporter = experimentExporter,
                context = context,
                onNavigateToResults = { navController.navigate("results") },
                onNavigateToTestCondition = { navController.navigate("testCondition") },
                lastSelectedRole = selectedRole,
                initialError = startError
            )
        }
        composable("testCondition") {
            TestConditionScreen(
                experimentController = experimentController,
                serviceController = serviceController,
                onBack = { navController.popBackStack() }
            )
        }
        composable("results") {
            ResultsScreen(
                measurementRecorder = measurementRecorder,
                experimentController = experimentController,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
