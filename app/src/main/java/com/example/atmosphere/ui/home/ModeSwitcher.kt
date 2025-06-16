package com.example.atmosphere.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.atmosphere.ui.composables.glassMorphism

@Composable
fun ModeSwitcher(
    modifier: Modifier = Modifier,
    currentMode: AppMode,
    onModeChange: (AppMode) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .glassMorphism(shape = CircleShape)
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeButton(
            text = "Live",
            isSelected = currentMode == AppMode.LIVE,
            onClick = { onModeChange(AppMode.LIVE) }
        )
        ModeButton(
            text = "History",
            isSelected = currentMode == AppMode.HISTORICAL,
            onClick = { onModeChange(AppMode.HISTORICAL) }
        )
        ModeButton(
            text = "Marine",
            isSelected = currentMode == AppMode.MARINE,
            onClick = { onModeChange(AppMode.MARINE) }
        )
    }
}

@Composable
private fun RowScope.ModeButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
        label = "modeButtonColor"
    )
    Text(
        text = text,
        color = Color.White,
        modifier = Modifier
            .weight(1f)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}