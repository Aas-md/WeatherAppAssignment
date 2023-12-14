package com.example.apnibus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepository {
    private val apiKey = "3ae38264582e4683942112949232406"

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://api.weatherapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val weatherApiService = retrofit.create(WeatherApiService::class.java)

    suspend fun getWeatherData(cityName: String): Weather {
        val response = weatherApiService.getWeatherData(apiKey, cityName)
        val current = response.current
        return Weather(current.temperature, current.condition.conditionText, current.condition.conditionIcon)
    }
}
