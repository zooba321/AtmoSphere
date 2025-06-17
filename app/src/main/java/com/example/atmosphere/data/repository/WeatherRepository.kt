package com.example.atmosphere.data.repository

import com.example.atmosphere.data.model.WeatherData
import com.example.atmosphere.data.remote.WeatherApiService
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class WeatherRepository(private val apiService: WeatherApiService) {

    private val apiKey = "dbf7f68f6062422bbe3121931251506" // <-- 在此處替換你的 API Key

    suspend fun getWeatherForecast(location: String): Result<WeatherData> {
        return try {
            val weatherData = apiService.getForecast(
                apiKey = apiKey,
                location = location
            )
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHistoricalWeather(location: String, date: LocalDate): Result<WeatherData> {
        return try {
            val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
            val weatherData = apiService.getHistoricalWeather(
                apiKey = apiKey,
                location = location,
                date = dateString
            )
            Result.success(weatherData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}