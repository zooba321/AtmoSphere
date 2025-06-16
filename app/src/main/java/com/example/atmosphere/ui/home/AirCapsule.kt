package com.example.atmosphere.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider // 關鍵修正 #1: Android Studio 可能會自動更新導入
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmosphere.data.model.AirQuality
import com.example.atmosphere.ui.composables.glassMorphism

@Composable
fun AirCapsule(
    airQuality: AirQuality?,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    if (airQuality == null) return

    val aqiIndex = airQuality.gb_defra_index
    val aqiColor by animateColorAsState(targetValue = getAqiColor(aqiIndex), label = "aqiColor")
    val aqiDescription = getAqiDescription(aqiIndex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .animateContentSize()
            .glassMorphism(
                shape = RoundedCornerShape(24.dp),
                borderColor = aqiColor.copy(alpha = 0.5f)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(aqiColor, CircleShape)
                        .blur(6.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "AQI: $aqiDescription",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Expand AQI",
                tint = Color.White.copy(alpha = 0.7f)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                // 關鍵修正 #2: 將 Divider 替換為 HorizontalDivider
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    AqiDetailItem("PM2.5", airQuality.pm2_5)
                    AqiDetailItem("O3", airQuality.o3)
                    AqiDetailItem("NO2", airQuality.no2)
                }
            }
        }
    }
}

@Composable
private fun AqiDetailItem(name: String, value: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = name, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
        Text(text = "%.1f".format(value), color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

private fun getAqiColor(index: Int): Color = when (index) {
    in 1..3 -> Color.Green
    in 4..6 -> Color.Yellow
    in 7..9 -> Color.Red
    10 -> Color(0xFF800080) // Purple
    else -> Color.Gray
}

private fun getAqiDescription(index: Int): String = when (index) {
    in 1..3 -> "Good"
    in 4..6 -> "Moderate"
    in 7..9 -> "High"
    10 -> "Very High"
    else -> "Unknown"
}