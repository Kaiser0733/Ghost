package com.ghost.blelab.ui.components

import androidx.compose.foundation.border.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black.copy(alpha = 0.87f),
        modifier = modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black.copy(alpha = 0.87f)
        )
    }
}

@Composable
fun StatusBadge(text: String, isActive: Boolean, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isActive) Color.White else Color.Black.copy(alpha = 0.6f),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(
                color = if (isActive) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled) Color(0xFF1565C0) else Color(0xFFBDBDBD),
            contentColor = Color.White
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF1565C0),
            border = BorderStroke(1.dp, Color(0xFF1565C0))
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RadioOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedState by remember { mutableStateOf(selected) }
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .background(
                color = if (selectedState) Color(0xFFE3F2FD) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = if (selectedState) 2.dp else 1.dp,
                color = if (selectedState) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                shape = RoundedCornerShape(8.dp)
            )
            .fillMaxWidth()
            .clickable { 
                selectedState = !selectedState
                onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, color = if (selectedState) Color(0xFF1565C0) else Color.Black.copy(alpha = 0.87f))
        RadioButton(
            selected = selectedState,
            onClick = { selectedState = !selectedState; onClick() },
            colors = RadioButtonDefaults.colors(
                selectedColor = Color(0xFF1565C0),
                unselectedColor = Color(0xFFBDBDBD)
            )
        )
    }
}

@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 12.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(BorderStroke(1.dp, Color(0xFFBDBDBD)), RoundedCornerShape(8.dp))
                    .wrapContentSize(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    ) {
                        Text(text = option, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}