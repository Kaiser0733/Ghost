package com.ghost.blelab.ui.navigation

import androidx.compose.runtime.Composable
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
    NavHost(navController, startDestination = "role") {
        composable("role") {
            RoleScreen(onRoleSelected = { role ->
                val config = ExperimentConfig(role = role)
                experimentController.startExperiment(config).getOrElse { throw it }
                navController.navigate("control")
            })
        }
        composable("control") {
            ControlScreen(
                experimentController = experimentController,
                bleAdvertiser = bleAdvertiser,
                bleScanner = bleScanner,
                measurementRecorder = measurementRecorder,
                experimentExporter = experimentExporter,
                context = context,
                onNavigateToResults = { navController.navigate("results") },
                onNavigateToTestCondition = { navController.navigate("testCondition") }
            )
        }
        composable("testCondition") {
            TestConditionScreen(
                experimentController = experimentController,
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