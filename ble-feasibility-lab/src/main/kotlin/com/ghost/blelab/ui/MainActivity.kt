package com.ghost.blelab.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.ghost.blelab.BleLabApplication
import com.ghost.blelab.ui.navigation.BleLabNavHost
import com.ghost.blelab.ui.theme.BleLabTheme
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {

    private val app by lazy { application as BleLabApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BleLabTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
                        BleLabNavHost(
                            experimentController = app.experimentController,
                            bleAdvertiser = app.bleAdvertiser,
                            bleScanner = app.bleScanner,
                            measurementRecorder = app.measurementRecorder,
                            experimentExporter = app.experimentExporter,
                            context = this
                        )
                    }
                }
            }
        }
    }
}