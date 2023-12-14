package com.example.apnibus

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var weatherViewModel: WeatherViewModel
    private var isCelsius = true
    private lateinit var requestQueue: RequestQueue
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        weatherViewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)
    toggleButton.text = "C"



        if (hasLocationPermission()) {
            fetchWeatherByLocation()
        } else {
            requestLocationPermission()
        }

    }


    private fun fetchWeatherByLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                if (addresses.isNotEmpty()) {
                    val cityName = addresses[0].locality
                    val countryName = addresses[0].countryName
                    weatherViewModel.fetchWeatherData(cityName)
                    observeWeatherData()
                    city_name.text = "$cityName, $countryName"
                } else {
                    Log.d(TAG,"Error : City Not found")
                }
            } else {
              Log.d(TAG,"Error location not available")
            }
        } catch (e: SecurityException) {

            Log.d(TAG,"Error SecurityException $e")
        }
    }

    private fun observeWeatherData() {
        weatherViewModel.currentWeather.observe(this, { weather ->
            // Update UI with weather data
            val temperature = if (isCelsius) {
                "${weather.temperature}°C"
            } else {
                val fahrenheit = weather.temperature.toDouble() * 9 / 5 + 32
                "${String.format("%.1f", fahrenheit)}°F"
            }
            text_temp.text = temperature
            condition_text.text = weather.conditionText
            Glide.with(this).load("https://${weather.conditionIcon}").into(condition_image)
        })
    }


    private fun hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}



    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_LOCATION_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchWeatherByLocation()
            } else {

                Log.d(TAG,"Error location permission denied")
            }
        }
    }

companion object {
    private const val REQUEST_LOCATION_PERMISSION = 123
}

    fun onToggleClick(view: android.view.View) {

        isCelsius = (view as ToggleButton).isChecked
        if(isCelsius)
            toggleButton.text = "C"
        else
            toggleButton.text = "F"
        observeWeatherData()
    }
}