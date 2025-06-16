package com.example.atmosphere.ui.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atmosphere.data.model.MarineData
import com.example.atmosphere.data.model.Tide
import com.example.atmosphere.ui.composables.glassMorphism
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun MarineBackground(isDay: Boolean) {
    val topColor = if (isDay) Color(0xFF87CEEB) else Color(0xFF000033)
    val midColor = if (isDay) Color(0xFFB2FFFF) else Color(0xFF2C3E50)

    val infiniteTransition = rememberInfiniteTransition(label = "wave_transition")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "wave_offset"
    )

    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .background(Brush.verticalGradient(listOf(topColor, midColor)))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.BottomCenter)
                .drawWithCache {
                    val wavePath = Path()
                    val waveBrush = Brush.verticalGradient(
                        listOf(Color(0xFF005A9C).copy(alpha = 0.8f), Color(0xFF003366))
                    )
                    onDrawBehind {
                        wavePath.reset()
                        val waveAmplitude = 20f
                        val waveFrequency = 0.02f
                        wavePath.moveTo(0f, waveAmplitude)
                        for (x in 0..size.width.toInt()) {
                            val y = waveAmplitude + sin((x * waveFrequency) + (waveOffset * 2 * PI.toFloat())) * waveAmplitude
                            wavePath.lineTo(x.toFloat(), y)
                        }
                        wavePath.lineTo(size.width, size.height)
                        wavePath.lineTo(0f, size.height)
                        wavePath.close()
                        drawPath(path = wavePath, brush = waveBrush)
                    }
                }
        )
    }
}

@Composable
fun MarineContent(marineData: MarineData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        WaveAndWindPanel(
            waveHeight = marineData.waveHeightM,
            windDegree = marineData.windDirDegree
        )
        TideChart(tides = marineData.tides)
    }
}

@Composable
fun WaveAndWindPanel(waveHeight: Float, windDegree: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .glassMorphism(shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Wave Height", color = Color.White.copy(alpha = 0.8f))
            Text("${waveHeight}m", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Wind Direction", color = Color.White.copy(alpha = 0.8f))
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Wind Direction",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .rotate(windDegree.toFloat())
            )
        }
    }
}

@Composable
fun TideChart(tides: List<Tide>, modifier: Modifier = Modifier) {
    if (tides.isEmpty()) return

    Column(
        modifier = modifier
            .glassMorphism(shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text("Tides", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        // 關鍵修正：使用 Box 和 drawWithCache，而不是直接在 Canvas 中繪圖
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(top = 16.dp)
            .drawWithCache {
                val maxTideHeight = tides.maxOfOrNull { it.heightMt } ?: 0f
                val minTideHeight = tides.minOfOrNull { it.heightMt } ?: 0f
                val heightRange = (maxTideHeight - minTideHeight).coerceAtLeast(1f)
                val points = tides.mapIndexed { index, tide ->
                    val x = size.width * (index.toFloat() / (tides.size - 1).coerceAtLeast(1))
                    val y = size.height * (1 - (tide.heightMt - minTideHeight) / heightRange)
                    Offset(x, y)
                }
                val path = if (points.isNotEmpty()) {
                    Path().apply {
                        moveTo(points.first().x, points.first().y)
                        for (i in 0 until points.size - 1) {
                            val p1 = points[i]
                            val p2 = points[i + 1]
                            val midPoint = Offset((p1.x + p2.x) / 2, (p1.y + p2.y) / 2)
                            quadraticBezierTo(p1.x, p1.y, midPoint.x, midPoint.y)
                        }
                        lineTo(points.last().x, points.last().y)
                    }
                } else Path()

                onDrawBehind {
                    drawPath(path, color = Color.White, style = Stroke(width = 4f))
                }
            }
        )
    }
}