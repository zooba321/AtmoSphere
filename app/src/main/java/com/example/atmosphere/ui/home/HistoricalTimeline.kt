package com.example.atmosphere.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HistoricalTimeline(
    modifier: Modifier = Modifier,
    progress: Float,
    selectedDate: LocalDate,
    onProgressChanged: (Float) -> Unit,
    onDragFinished: () -> Unit
) {
    val timelineHeight = 300.dp

    val animatedBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF8A2BE2), Color(0xFF4169E1), Color(0xFF00BFFF),
            Color(0xFF32CD32), Color(0xFFFFD700), Color(0xFFFFA500)
        )
    )

    Box(
        modifier = modifier
            .width(60.dp)
            .height(timelineHeight)
            .clip(RoundedCornerShape(30.dp))
            .background(animatedBrush)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = { onDragFinished() }
                ) { change, _ ->
                    // 關鍵修正：使用 change.position.y 而不是 change.y
                    val dragAmount = change.position.y - change.previousPosition.y
                    val newProgress = (progress + (dragAmount / size.height)).coerceIn(0f, 1f)
                    onProgressChanged(newProgress)
                    change.consume()
                }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (timelineHeight - 16.dp) * progress)
                .size(16.dp)
                .background(Color.White, CircleShape)
        )
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("MM-dd")),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .rotate(-90f)
        )
    }
}