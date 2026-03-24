package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.model.DailyForecast

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather
    suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast>
}
