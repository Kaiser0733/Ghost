package com.ghost.blelab.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Standard scrollable screen container for the lab UI.
 *
 * Every lab screen routes its content through this so that:
 * - content taller than the viewport scrolls instead of clipping
 *   (Galaxy A03 has a small 720p screen; long screens MUST scroll),
 * - content is never hidden behind status/navigation bars or the IME
 *   (edge-to-edge safe on Android 13 and 16 / One UI).
 */
@Composable
fun LabScrollScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}

/**
 * Non-scrolling full-size container with system-bar insets applied.
 * For screens that center a small amount of content (loading/error).
 */
@Composable
fun LabInsetScreen(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(24.dp),
        content = content
    )
}

/**
 * Inline error banner. Used to surface action failures (start/stop/export)
 * instead of failing silently.
 */
@Composable
fun ErrorBanner(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        fontSize = 14.sp,
        color = Color(0xFFB71C1C),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEBEE), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFEF9A9A), RoundedCornerShape(8.dp))
            .padding(12.dp)
    )
}

/**
 * Inline success/info banner (e.g. export completed).
 */
@Composable
fun InfoBanner(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        fontSize = 14.sp,
        color = Color(0xFF1B5E20),
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(8.dp))
            .padding(12.dp)
    )
}
