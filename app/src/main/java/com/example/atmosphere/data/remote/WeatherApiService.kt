package com.example.atmosphere.data.remote

import com.example.atmosphere.data.model.WeatherData
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 14,
        @Query("aqi") aqi: String = "yes",
        @Query("alerts") alerts: String = "yes",
        @Query("tides") tides: String = "yes"
    ): WeatherData

    @GET("history.json")
    suspend fun getHistoricalWeather(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("dt") date: String,
        @Query("aqi") aqi: String = "yes"
    ): WeatherData
}