package com.example.atmosphere.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.atmosphere.ui.common.HandleLocationPermission
import com.example.atmosphere.utils.ViewModelFactory

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel(factory = ViewModelFactory())) {
    HandleLocationPermission(
        onPermissionGranted = {
            // 權限被授予是觸發數據加載的唯一入口點，邏輯清晰
            viewModel.loadWeatherData("auto:ip")
        }
    ) {
        // 只有在權限被授予後才會顯示以下內容
        val uiState by viewModel.uiState.collectAsState()
        val weatherData = uiState.weatherData

        Box(modifier = Modifier.fillMaxSize()) {
            if (weatherData != null) {
                when (uiState.appMode) {
                    AppMode.MARINE -> MarineBackground(isDay = weatherData.current.is_day == 1)
                    else -> AtmosphericBackground(
                        isDay = weatherData.current.is_day == 1,
                        // 關鍵修正：使用正確的路徑 .condition.text
                        conditionText = weatherData.current.condition.text,
                        windSpeedKph = weatherData.current.wind_kph,
                        timelineProgress = uiState.timelineProgress,
                        isTimelineDragging = uiState.isTimelineDragging
                    )
                }
            }

            // 根據 ViewModel 的狀態來決定顯示什麼
            if (uiState.isLoading && weatherData == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            } else if (weatherData != null) {
                WeatherContent(
                    appMode = uiState.appMode,
                    weatherData = weatherData,
                    marineData = uiState.marineData,
                    selectedHourIndex = uiState.selectedHourIndex,
                    isAqiExpanded = uiState.isAqiExpanded,
                    historicalDate = uiState.historicalDate,
                    timelineProgress = uiState.timelineProgress,
                    onHourSelected = viewModel::selectHour,
                    onAqiClick = viewModel::toggleAqiExpansion,
                    onModeChange = viewModel::setMode,
                    onTimelineDrag = viewModel::onTimelineDrag,
                    onTimelineDragFinished = viewModel::onTimelineDragFinished
                )
            } else if (uiState.errorMessage != null) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // 如果既不加載，也沒有數據和錯誤（初始狀態），則顯示一個空白 Box，等待權限回呼觸發加載
            else {
                Box(modifier = Modifier.fillMaxSize())
            }
        }
    }
}