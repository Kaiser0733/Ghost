package com.ghost.blelab.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.ghost.blelab.ui.components.PrimaryButton
import com.ghost.blelab.ui.components.RadioOption
import com.ghost.blelab.ui.components.LabScrollScreen

@Composable
fun RoleScreen(
    onRoleSelected: (com.ghost.blelab.experiment.Role) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRole by remember { mutableStateOf<com.ghost.blelab.experiment.Role?>(null) }

    LabScrollScreen(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "BLE Feasibility Lab",
                fontSize = 28.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = androidx.compose.ui.graphics.Color(0xFF1565C0)
            )
            Text(
                text = "Select your role for this experiment",
                fontSize = 16.sp,
                color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            RadioOption(
                text = "Advertiser (Broadcasts)",
                selected = selectedRole == com.ghost.blelab.experiment.Role.ADVERTISER,
                onClick = { selectedRole = com.ghost.blelab.experiment.Role.ADVERTISER }
            )

            RadioOption(
                text = "Scanner (Detects)",
                selected = selectedRole == com.ghost.blelab.experiment.Role.SCANNER,
                onClick = { selectedRole = com.ghost.blelab.experiment.Role.SCANNER }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "Continue",
                onClick = { selectedRole?.let { onRoleSelected(it) } },
                enabled = selectedRole != null
            )
        }
    }
}
