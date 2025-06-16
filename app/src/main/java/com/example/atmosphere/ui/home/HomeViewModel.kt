package com.example.atmosphere.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.atmosphere.data.model.ForecastDay
import com.example.atmosphere.data.model.Hour
import com.example.atmosphere.data.model.MarineData
import com.example.atmosphere.data.model.Tide
import com.example.atmosphere.data.model.WeatherData
import com.example.atmosphere.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class AppMode {
    LIVE, HISTORICAL, MARINE
}

data class HomeUiState(
    val isLoading: Boolean = true,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null,
    val selectedHourIndex: Int = 0,
    val isAqiExpanded: Boolean = false,
    val appMode: AppMode = AppMode.LIVE,
    val marineData: MarineData? = null,
    val historicalDate: LocalDate = LocalDate.now(),
    val timelineProgress: Float = 0f,
    val isTimelineDragging: Boolean = false
)

class HomeViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private var historicalFetchJob: Job? = null


    fun loadWeatherData(location: String) {
        // 防止在已有數據時因權限回呼重複加載
        if (_uiState.value.weatherData != null && location == "auto:ip") return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) } // 開始加載時清除舊錯誤
            repository.getWeatherForecast(location).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        weatherData = data,
                        selectedHourIndex = findCurrentHourIndex(data.forecast.forecastday.first().hour),
                        marineData = parseMarineDataFromForecast(data.forecast.forecastday.first())
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message)
                }
            }
        }
    }

    fun selectHour(index: Int) {
        _uiState.update { it.copy(selectedHourIndex = index) }
    }

    fun toggleAqiExpansion() {
        _uiState.update { it.copy(isAqiExpanded = !it.isAqiExpanded) }
    }

    fun setMode(mode: AppMode) {
        _uiState.update { it.copy(appMode = mode) }
        if (mode == AppMode.LIVE && _uiState.value.historicalDate != LocalDate.now()) {
            loadWeatherData(_uiState.value.weatherData?.location?.name ?: "auto:ip")
        }
    }

    fun onTimelineDrag(progress: Float) {
        val daysAgo = (progress * 365).toLong()
        val selectedDate = LocalDate.now().minusDays(daysAgo)
        _uiState.update {
            it.copy(
                timelineProgress = progress,
                historicalDate = selectedDate,
                isTimelineDragging = true
            )
        }
    }

    fun onTimelineDragFinished() {
        _uiState.update { it.copy(isTimelineDragging = false) }
        fetchHistoricalData(_uiState.value.historicalDate)
    }

    private fun fetchHistoricalData(date: LocalDate) {
        historicalFetchJob?.cancel()
        historicalFetchJob = viewModelScope.launch {
            if (date.isEqual(LocalDate.now())) {
                setMode(AppMode.LIVE)
                loadWeatherData(_uiState.value.weatherData?.location?.name ?: "auto:ip")
                return@launch
            }
            _uiState.update { it.copy(isLoading = true) }
            val location = _uiState.value.weatherData?.location?.name ?: "auto:ip"
            repository.getHistoricalWeather(location, date).onSuccess { data ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        weatherData = data,
                        selectedHourIndex = 12 // 重置為中午12點
                    )
                }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    private fun findCurrentHourIndex(hourlyForecast: List<Hour>): Int {
        val now = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return hourlyForecast.indexOfFirst {
            try {
                val hourTime = LocalDateTime.parse(it.time, formatter)
                hourTime.hour == now.hour
            } catch (e: Exception) {
                false
            }
        }.coerceAtLeast(0)
    }

    private fun parseMarineDataFromForecast(forecastDay: ForecastDay): MarineData? {
        val tides = forecastDay.astro.tide?.mapNotNull { apiTide ->
            val height = apiTide.tide_height_mt?.toFloatOrNull()
            if (height != null) {
                Tide(
                    time = apiTide.tide_time,
                    type = apiTide.tide_type,
                    heightMt = height
                )
            } else {
                null
            }
        }
        if (tides.isNullOrEmpty()) return null

        return MarineData(
            waveHeightM = forecastDay.day.avg_wave_m ?: 0f,
            windDirDegree = forecastDay.hour.firstOrNull()?.wind_degree ?: 0,
            tides = tides
        )
    }
}