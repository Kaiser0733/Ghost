package com.ghost.blelab

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ghost.blelab.ble.advertiser.BleAdvertiser
import com.ghost.blelab.ble.advertiser.BleAdvertiserImpl
import com.ghost.blelab.ble.scanner.BleScanner
import com.ghost.blelab.ble.scanner.BleScannerImpl
import com.ghost.blelab.experiment.ExperimentController
import com.ghost.blelab.experiment.ExperimentControllerImpl
import com.ghost.blelab.experiment.TestCondition
import com.ghost.blelab.export.ExperimentExporter
import com.ghost.blelab.export.ExperimentExporterImpl
import com.ghost.blelab.measurement.MeasurementRecorder
import com.ghost.blelab.measurement.MeasurementRecorderImpl

class BleLabApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // No initialization needed for this experiment
    }

    // Provide dependencies
    private val bluetoothManager by lazy { getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter by lazy { bluetoothManager.adapter }

    val bleAdvertiser: BleAdvertiser by lazy {
        BleAdvertiserImpl(this, bluetoothAdapter)
    }

    val bleScanner: BleScanner by lazy {
        BleScannerImpl(this, bluetoothAdapter)
    }

    val measurementRecorder: MeasurementRecorder by lazy {
        MeasurementRecorderImpl(this)
    }

    val experimentController: ExperimentController by lazy {
        ExperimentControllerImpl(this, bleAdvertiser, bleScanner, measurementRecorder)
    }

    val experimentExporter: com.ghost.blelab.export.ExperimentExporter by lazy {
        com.ghost.blelab.export.ExperimentExporterImpl(this, measurementRecorder)
    }

    /**
     * Test condition selected in the UI before an experiment run exists.
     *
     * ExperimentControllerImpl.setTestCondition is a no-op while no run is
     * active, so conditions chosen on the Test Condition screen were silently
     * lost. The foreground service merges this value into the config when the
     * next experiment starts, then clears it. In-memory only — never persisted.
     */
    @Volatile
    var pendingTestCondition: TestCondition? = null
}
