package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.model.DailyForecast
import java.io.IOException

class FakeWeatherRepository : WeatherRepository {

    var currentWeatherResult: CurrentWeather = CurrentWeather(
        temperature = 28.5,
        tempMax = 32.0,
        tempMin = 25.0,
        feelsLike = 31.2,
        humidity = 70,
        windSpeed = 3.5,
        windDeg = 180,
        condition = "Clouds",
        conditionDescription = "scattered clouds",
        iconCode = "03d",
        timestamp = 1711296000L,
    )

    var dailyForecastsResult: List<DailyForecast> = listOf(
        DailyForecast(date = "2026-03-24", tempMax = 30.0, tempMin = 27.0, condition = "Clouds", iconCode = "03d"),
        DailyForecast(date = "2026-03-25", tempMax = 25.0, tempMin = 18.0, condition = "Rain", iconCode = "10d"),
    )

    var shouldThrowError: Boolean = false

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather {
        if (shouldThrowError) throw IOException("Fake error")
        return currentWeatherResult
    }

    override suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast> {
        if (shouldThrowError) throw IOException("Fake error")
        return dailyForecastsResult
    }
}
