package com.example.apnibus

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("current")
    val current: CurrentWeather
)

data class CurrentWeather(
    @SerializedName("temp_c")
    val temperature: String,
    @SerializedName("condition")
    val condition: Condition
)

data class Condition(
    @SerializedName("text")
    val conditionText: String,
    @SerializedName("icon")
    val conditionIcon: String
)
