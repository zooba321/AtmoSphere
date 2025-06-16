package com.example.atmosphere.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.atmosphere.data.model.MarineData
import com.example.atmosphere.data.model.WeatherData
import java.time.LocalDate

@Composable
fun WeatherContent(
    appMode: AppMode,
    weatherData: WeatherData,
    marineData: MarineData?,
    selectedHourIndex: Int,
    isAqiExpanded: Boolean,
    historicalDate: LocalDate,
    timelineProgress: Float,
    onHourSelected: (Int) -> Unit,
    onAqiClick: () -> Unit,
    onModeChange: (AppMode) -> Unit,
    onTimelineDrag: (Float) -> Unit,
    onTimelineDragFinished: () -> Unit
) {
    val forecastDay = weatherData.forecast.forecastday.first()
    val selectedHourData = forecastDay.hour.getOrElse(selectedHourIndex) { forecastDay.hour.first() }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            ModeSwitcher(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                currentMode = appMode,
                onModeChange = onModeChange
            )
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            if (appMode == AppMode.HISTORICAL) {
                HistoricalTimeline(
                    modifier = Modifier.padding(top = 16.dp),
                    progress = timelineProgress,
                    selectedDate = historicalDate,
                    onProgressChanged = onTimelineDrag,
                    onDragFinished = onTimelineDragFinished
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MainGlassPanel(
                    weather = weatherData,
                    selectedHour = selectedHourData,
                    isAqiExpanded = isAqiExpanded,
                    onAqiClick = onAqiClick,
                    appMode = appMode
                )

                Spacer(Modifier.height(24.dp))

                AnimatedContent(
                    targetState = appMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "mode_content_transition"
                ) { targetMode ->
                    when (targetMode) {
                        AppMode.LIVE -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            HourlyForecastStrip(
                                hours = forecastDay.hour,
                                selectedIndex = selectedHourIndex,
                                onHourSelected = onHourSelected
                            )
                            Spacer(Modifier.height(24.dp))
                            DailyForecastList(forecastDays = weatherData.forecast.forecastday)
                        }
                        AppMode.MARINE -> if (marineData != null) {
                            MarineContent(marineData = marineData)
                        } else {
                            Text("Marine data not available.", color = Color.White)
                        }
                        AppMode.HISTORICAL -> DailyForecastList(forecastDays = weatherData.forecast.forecastday)
                    }
                }
            }
        }
    }
}