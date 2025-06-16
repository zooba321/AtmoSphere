package com.example.atmosphere.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.atmosphere.data.model.Hour
import com.example.atmosphere.ui.composables.glassMorphism
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@Composable
fun HourlyForecastStrip(
    hours: List<Hour>,
    selectedIndex: Int,
    onHourSelected: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0 && selectedIndex < hours.size) {
            listState.animateScrollToItem(selectedIndex)
        }
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        itemsIndexed(hours) { index, hour ->
            HourlyItem(
                hour = hour,
                isSelected = index == selectedIndex,
                onClick = { onHourSelected(index) }
            )
        }
    }
}

@Composable
fun HourlyItem(hour: Hour, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent
    val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    val displayTime = try {
        LocalDateTime.parse(hour.time, timeFormatter).format(DateTimeFormatter.ofPattern("HH:mm"))
    } catch (e: Exception) {
        hour.time.takeLast(5)
    }

    Column(
        modifier = Modifier
            .glassMorphism(
                shape = RoundedCornerShape(16.dp),
                backgroundColor = backgroundColor
            )
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = displayTime, color = Color.White, fontSize = 14.sp)
        AsyncImage(
            model = "https:${hour.condition.icon}",
            contentDescription = hour.condition.text,
            modifier = Modifier.size(40.dp)
        )
        Text(text = "${hour.temp_c.roundToInt()}°", color = Color.White, fontWeight = FontWeight.Bold)
    }
}