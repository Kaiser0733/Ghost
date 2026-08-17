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
fun RoleScreen(
    onRoleSelected: (com.ghost.blelab.experiment.Role) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedRole by remember { mutableStateOf<com.ghost.blelab.experiment.Role?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "BLE Feasibility Lab",
            fontSize = 28.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.graphics.Color(0xFF1565C0)
        )
        Text(
            text = "Select your role for this experiment",
            fontSize = 16.sp,
            color = androidx.compose.graphics.Color.Black.copy(alpha = 0.6f)
        )

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))

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

        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))

        PrimaryButton(
            text = "Continue",
            onClick = { selectedRole?.let { onRoleSelected(it) } },
            enabled = selectedRole != null
        )
    }
}