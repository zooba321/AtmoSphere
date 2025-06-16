package com.example.atmosphere.data.model

data class MarineData(
    val waveHeightM: Float,
    val windDirDegree: Int,
    val tides: List<Tide>
)

data class Tide(
    val time: String,
    val type: String,
    val heightMt: Float
)