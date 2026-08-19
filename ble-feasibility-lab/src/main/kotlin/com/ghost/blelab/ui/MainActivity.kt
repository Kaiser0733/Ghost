package com.ghost.blelab.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ghost.blelab.BleLabApplication
import com.ghost.blelab.ui.navigation.BleLabNavHost
import com.ghost.blelab.ui.screens.ErrorScreen
import com.ghost.blelab.ui.theme.BleLabTheme

class MainActivity : ComponentActivity() {

    private val app by lazy { application as BleLabApplication }

    private var permissionsGranted by mutableStateOf(false)

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grants ->
            permissionsGranted = grants.values.all { it }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsGranted = hasAllRequiredPermissions()
        if (!permissionsGranted) {
            requestRequiredPermissions()
        }

        val appContext = this
        setContent {
            BleLabTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    if (permissionsGranted) {
                        BleLabNavHost(
                            experimentController = app.experimentController,
                            bleAdvertiser = app.bleAdvertiser,
                            bleScanner = app.bleScanner,
                            measurementRecorder = app.measurementRecorder,
                            experimentExporter = app.experimentExporter,
                            context = appContext
                        )
                    } else {
                        ErrorScreen(
                            message = "The BLE Feasibility Lab needs Bluetooth " +
                                "(and notification) permissions to run the experiment. " +
                                "Grant them to continue.",
                            onRetry = {
                                if (hasAllRequiredPermissions()) {
                                    permissionsGranted = true
                                } else {
                                    requestRequiredPermissions()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    private fun requiredPermissions(): Array<String> {
        val perms = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            perms.add(Manifest.permission.BLUETOOTH_SCAN)
            perms.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            perms.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return perms.toTypedArray()
    }

    private fun hasAllRequiredPermissions(): Boolean {
        val perms = requiredPermissions()
        if (perms.isEmpty()) return true
        return perms.all { checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
    }

    private fun requestRequiredPermissions() {
        val perms = requiredPermissions()
        if (perms.isEmpty()) {
            permissionsGranted = true
            return
        }
        permissionLauncher.launch(perms)
    }
}
