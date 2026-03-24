package playground.app.tobeylin.weather.core.network

import playground.app.tobeylin.weather.core.network.model.NetworkCurrentWeatherResponse
import playground.app.tobeylin.weather.core.network.model.NetworkForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
    ): NetworkCurrentWeatherResponse

    @GET("data/2.5/forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String = "metric",
    ): NetworkForecastResponse
}
