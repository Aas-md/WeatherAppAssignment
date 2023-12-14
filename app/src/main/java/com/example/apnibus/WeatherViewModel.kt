package com.example.apnibus

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository = WeatherRepository()

    private val _currentWeather = MutableLiveData<Weather>()
    val currentWeather: LiveData<Weather> = _currentWeather

    fun fetchWeatherData(cityName: String) {
        viewModelScope.launch {
            try {
                val weather = weatherRepository.getWeatherData(cityName)
                _currentWeather.value = weather
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching weather data: ${e.message}")
            }
        }
    }
}
