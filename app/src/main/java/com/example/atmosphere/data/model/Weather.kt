package com.example.atmosphere.data.model

import com.google.gson.annotations.SerializedName

// ... (所有 data class 內容保持不變) ...
data class WeatherData(
    val location: Location,
    val current: Current,
    val forecast: Forecast,
    val alerts: Alerts? = null
)

data class Location(val name: String, val region: String, val localtime: String)

data class Current(
    val temp_c: Float,
    val feelslike_c: Float,
    val is_day: Int,
    val condition: Condition,
    val wind_kph: Float,
    val humidity: Int,
    val uv: Float,
    val air_quality: AirQuality? = null
)

data class Condition(val text: String, val icon: String)

data class AirQuality(val pm2_5: Float, val o3: Float, val co: Float, val so2: Float, val no2: Float, @SerializedName("gb-defra-index") val gb_defra_index: Int)

data class Forecast(val forecastday: List<ForecastDay>)

data class ForecastDay(
    val date: String,
    val day: Day,
    val astro: Astro,
    val hour: List<Hour>
)

data class Day(
    val maxtemp_c: Float,
    val mintemp_c: Float,
    val condition: Condition,
    @SerializedName("maxwave_m") val avg_wave_m: Float?,
    @SerializedName("totalprecip_mm") val totalprecip_mm: Float?,
    @SerializedName("avgvis_km") val avgvis_km: Float?
)

data class Astro(
    val sunrise: String,
    val sunset: String,
    val moonrise: String,
    val moonset: String,
    val moon_phase: String,
    val tide: List<ApiTide>?
)

data class Hour(
    val time: String,
    val temp_c: Float,
    val condition: Condition,
    val chance_of_rain: Int,
    val chance_of_snow: Int,
    @SerializedName("wind_degree") val wind_degree: Int?
)

data class Alerts(val alert: List<AlertItem>)
data class AlertItem(val headline: String, val event: String, val desc: String)

data class ApiTide(
    @SerializedName("tide_time") val tide_time: String,
    @SerializedName("tide_type") val tide_type: String,
    @SerializedName("tide_height_mt") val tide_height_mt: String?
)