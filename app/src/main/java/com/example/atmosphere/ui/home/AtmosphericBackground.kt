package com.example.atmosphere.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import kotlinx.coroutines.delay
import kotlin.random.Random

// 關鍵修正：將所有輔助類和函數放回檔案頂層，確保可被訪問
data class Particle(
    var position: Offset,
    var velocity: Offset,
    var color: Color,
    var size: Float
)

enum class WeatherEffect {
    NONE, RAIN, SNOW
}

@Composable
fun AtmosphericBackground(
    isDay: Boolean,
    conditionText: String,
    windSpeedKph: Float,
    timelineProgress: Float,
    isTimelineDragging: Boolean
) {
    val baseTopColor = if (isDay) Color(0xFF87CEEB) else Color(0xFF000033)
    val baseBottomColor = if (isDay) Color(0xFFB2FFFF) else Color(0xFF2C3E50)

    val winterColor = Color(0xFFADD8E6)
    val springColor = Color(0xFF90EE90)
    val summerColor = Color(0xFFFFFACD)
    val autumnColor = Color(0xFFF4A460)

    val seasonalColor = remember(timelineProgress) {
        val p = timelineProgress * 4
        when {
            p < 1f -> lerp(winterColor, springColor, p)
            p < 2f -> lerp(springColor, summerColor, p - 1f)
            p < 3f -> lerp(summerColor, autumnColor, p - 2f)
            else -> lerp(autumnColor, winterColor, p - 3f)
        }
    }

    val targetTopColor = if (isTimelineDragging) lerp(baseTopColor, seasonalColor, 0.3f) else baseTopColor
    val targetBottomColor = if (isTimelineDragging) lerp(baseBottomColor, seasonalColor, 0.5f) else baseBottomColor

    val animatedTopColor by animateColorAsState(targetValue = targetTopColor, animationSpec = tween(1000), label = "topColor")
    val animatedBottomColor by animateColorAsState(targetValue = targetBottomColor, animationSpec = tween(1000), label = "bottomColor")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(animatedTopColor, animatedBottomColor)))
    )

    val weatherEffect = when {
        "rain" in conditionText.lowercase() -> WeatherEffect.RAIN
        "snow" in conditionText.lowercase() || "sleet" in conditionText.lowercase() -> WeatherEffect.SNOW
        else -> WeatherEffect.NONE
    }

    val particles = rememberParticleState(
        weatherEffect = weatherEffect,
        isDay = isDay,
        windSpeedKph = windSpeedKph
    )

    LaunchedEffect(Unit) {
        while (true) {
            particles.forEach { p ->
                p.position = Offset(
                    (p.position.x + p.velocity.x).let { if (it < 0) 1f + (it % 1f) else it % 1.0f },
                    (p.position.y + p.velocity.y) % 1.0f
                )
            }
            delay(16L)
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        particles.forEach { p ->
            val particlePos = Offset(p.position.x * canvasWidth, p.position.y * canvasHeight)
            when (weatherEffect) {
                WeatherEffect.RAIN -> {
                    drawLine(
                        color = p.color,
                        start = particlePos,
                        end = Offset(particlePos.x + p.velocity.x * 1000, particlePos.y + p.velocity.y * 1000),
                        strokeWidth = p.size
                    )
                }
                WeatherEffect.SNOW -> {
                    drawCircle(
                        color = p.color,
                        radius = p.size,
                        center = particlePos
                    )
                }
                WeatherEffect.NONE -> {}
            }
        }
    }
}

@Composable
fun rememberParticleState(
    weatherEffect: WeatherEffect,
    isDay: Boolean,
    windSpeedKph: Float
): List<Particle> {
    val particles = remember { mutableStateListOf<Particle>() }
    LaunchedEffect(weatherEffect) {
        particles.clear()
        val particleCount = when (weatherEffect) {
            WeatherEffect.RAIN -> 200
            WeatherEffect.SNOW -> 100
            else -> 0
        }
        val windFactor = (windSpeedKph / 15f).coerceIn(-2f, 2f)

        for (i in 0 until particleCount) {
            particles.add(
                Particle(
                    position = Offset(Random.nextFloat(), Random.nextFloat()),
                    velocity = when (weatherEffect) {
                        WeatherEffect.RAIN -> Offset(0.001f * windFactor, 0.015f)
                        WeatherEffect.SNOW -> Offset(0.0005f * windFactor + Random.nextFloat() * 0.001f - 0.0005f, 0.002f)
                        else -> Offset.Zero
                    },
                    color = if (isDay) Color.White.copy(alpha = 0.7f) else Color.LightGray.copy(alpha = 0.6f),
                    size = when (weatherEffect) {
                        WeatherEffect.RAIN -> Random.nextFloat() * 1.5f + 1f
                        WeatherEffect.SNOW -> Random.nextFloat() * 3f + 2f
                        else -> 0f
                    }
                )
            )
        }
    }
    return particles
}