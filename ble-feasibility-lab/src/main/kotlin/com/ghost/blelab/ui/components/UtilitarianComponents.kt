package com.ghost.blelab.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    androidx.compose.material3.Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = if (isActive) Color.White else Color.Black.copy(alpha = 0.6f),
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(
                color = if (isActive) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
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
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
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
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF1565C0),
            outlinedBorder = androidx.compose.foundation.border.BorderStroke(1.dp, Color(0xFF1565C0))
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp)
            .background(
                color = if (selected) Color(0xFFE3F2FD) else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color(0xFF1565C0) else Color(0xFFBDBDBD),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 16.sp, color = if (selected) Color(0xFF1565C0) else Color.Black.copy(alpha = 0.87f))
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick,
            colors = androidx.compose.material3.RadioButtonDefaults.colors(
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
    Column(modifier = modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.6f))
        androidx.compose.material3.Menu(
            onDismissRequest = {},
            modifier = modifier.fillMaxWidth()
        ) {
            androidx.compose.material3.ExposedDropdownMenuBox(
                expanded = { _, _ -> Unit },
                modifier = Modifier.fillMaxWidth()
            ) { expanded ->
                Text(
                    text = value,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 12.dp)
                        .background(
                            color = Color.White,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .border(1.dp, Color(0xFFBDBDBD), androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                        .wrapContentSize(),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.TextOverflow.Ellipsis
                )
                androidx.compose.material3.DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { /* handled by ExposedDropdownMenuBox */ }
                ) {
                    options.forEach { option ->
                        androidx.compose.material3.DropdownMenuItem(
                            onClick = {
                                onValueChange(option)
                            }
                        ) {
                            Text(text = option, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}