package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ghost.blelab.ui.components.InfoRow
import com.ghost.blelab.ui.components.LabScrollScreen
import com.ghost.blelab.ui.components.SecondaryButton
import com.ghost.blelab.ui.components.SectionTitle

@Composable
fun ResultsScreen(
    measurementRecorder: com.ghost.blelab.measurement.MeasurementRecorder,
    experimentController: com.ghost.blelab.experiment.ExperimentController,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val run = experimentController.getCurrentRun()
    val stats = run?.let { measurementRecorder.getAggregatedStats(it.id).getOrNull() }

    LabScrollScreen(modifier = modifier) {
        Text(
            text = "Results",
            fontSize = 24.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.ui.graphics.Color(0xFF1565C0)
        )

        stats?.let { s ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SectionTitle("Detection Statistics")
                InfoRow("Total Scans", s.totalScans.toString())
                InfoRow("Detections", s.detections.toString())
                InfoRow("Detection Rate", "%.1f%%".format(s.detectionRate * 100))

                SectionTitle("RSSI Statistics")
                InfoRow("Average RSSI", "%.1f dBm".format(s.averageRssi))
                InfoRow("RSSI Variance", "%.1f".format(s.rssiVariance))
                InfoRow("Min RSSI", "${s.minRssi} dBm")
                InfoRow("Max RSSI", "${s.maxRssi} dBm")

                SectionTitle("Latency Statistics")
                InfoRow("Min Latency", "${s.latencyStats.minLatencyMs} ms")
                InfoRow("Max Latency", "${s.latencyStats.maxLatencyMs} ms")
                InfoRow("Avg Latency", "%.1f ms".format(s.latencyStats.avgLatencyMs))
                InfoRow("Median Latency", "${s.latencyStats.medianLatencyMs} ms")
            }
        } ?: Text(
            text = "No experiment data available. Start an experiment first.",
            fontSize = 16.sp,
            color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        SecondaryButton(
            text = "Back",
            onClick = onBack
        )
    }
}
