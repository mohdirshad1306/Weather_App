package com.mastercoding.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.mastercoding.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Api :-> 1f47425c10cdb14c1e07c6ee5d99908f
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // fetch the data //
        fetchWeatherData("Jaipur")
        // function for the search City //
        SearchCity()
    }

    private fun SearchCity() {
       val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {


                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
               return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterFace::class.java)
        val response =
            retrofit.getWeatherData(cityName, "1f47425c10cdb14c1e07c6ee5d99908f", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            // jo bhi response aayega toh kya hona chahiye //
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    // set kar lete ab //

                    // XML ki id ko set kar sakte binding kar ke //
                    binding.temp.text = "$temperature °C"
                    binding.weather.text = condition
                    binding.maxTemp.text = "Max Temp: $maxTemp °C"
                    binding.minTemp.text = "Min Temp: $minTemp °C"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.sea.text = "$seaLevel hPa"
                    binding.condition.text = condition
                    binding.day.text = dayName(System.currentTimeMillis())
                        binding.date.text = date()
                        binding.cityName.text = "$cityName"


                    // API ko check karne ke liye use kiya hai
                    //  Log.d("TAG", "onResponse: $temperature")

                    // temperation ke condition par background change hoga //
                    changeImageAccordingToWeatherCondition(condition)
                }
            }

            // agar response nhi aayega toh kya hona chahiye //
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun changeImageAccordingToWeatherCondition(conditions: String) {
       when(conditions){
           "Clear Sky" , "Sunny" , "Clear" ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
           "Partly Clouds" , "Clouds" , "Overcase" , "Mist" , "Foggy" ->{
               binding.root.setBackgroundResource(R.drawable.colud_background)
               binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }
           "Light Rain" , "Drizzle" , "Moderate Rain" , "Showers" , "Heavy Rain" ->{
               binding.root.setBackgroundResource(R.drawable.rain_background)
               binding.lottieAnimationView.setAnimation(R.raw.rain)
           }
           "Light Snow" , "Moderate Snow" , "Heavy Snow" , "Blizzard" ->{
               binding.root.setBackgroundResource(R.drawable.snow_background)
               binding.lottieAnimationView.setAnimation(R.raw.snow)
           }
           else ->{
               binding.root.setBackgroundResource(R.drawable.sunny_background)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
       }
        binding.lottieAnimationView.playAnimation()
    }
  // date ko set karne ke liye //
    private fun date(): String {
        val simpleDayFormat = SimpleDateFormat("dd MMMM yyyy" , Locale.getDefault())
        return simpleDayFormat.format((Date()))
    }
 // Sun rise or Sun set ko set karne ke liye //
    private fun time(timestamp: Long): String {
        val simpleDayFormat = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return simpleDayFormat.format((Date(timestamp*1000)))
    }

// day ko set karne ke liye //
    fun dayName(timestamp: Long): String{
            val simpleDayFormat = SimpleDateFormat("EEEE" , Locale.getDefault())
            return simpleDayFormat.format((Date()))
        }

}


