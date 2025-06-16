package com.example.atmosphere.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmosphere.data.model.Hour
import com.example.atmosphere.data.model.WeatherData
import com.example.atmosphere.ui.composables.glassMorphism
import kotlin.math.roundToInt

@Composable
fun MainGlassPanel(
    weather: WeatherData,
    selectedHour: Hour,
    isAqiExpanded: Boolean,
    onAqiClick: () -> Unit,
    appMode: AppMode
) {
    val animatedTemp by animateFloatAsState(
        targetValue = selectedHour.temp_c,
        animationSpec = tween(durationMillis = 500), label = "temp_animation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassMorphism(shape = RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (appMode == AppMode.HISTORICAL) {
            Text(
                "Historical Data",
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Text(
            text = weather.location.name,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${animatedTemp.roundToInt()}°",
            fontSize = 80.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = selectedHour.condition.text,
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 20.sp
        )

        AirCapsule(
            airQuality = weather.current.air_quality,
            isExpanded = isAqiExpanded,
            onClick = onAqiClick
        )
    }
}