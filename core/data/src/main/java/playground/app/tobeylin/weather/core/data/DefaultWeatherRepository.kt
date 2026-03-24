package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.data.mapper.asExternalModel
import playground.app.tobeylin.weather.core.data.mapper.toDailyForecasts
import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.model.DailyForecast
import playground.app.tobeylin.weather.core.network.WeatherApi
import javax.inject.Inject

internal class DefaultWeatherRepository @Inject constructor(
    private val weatherApi: WeatherApi,
) : WeatherRepository {
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather =
        weatherApi.getCurrentWeather(latitude, longitude).asExternalModel()

    override suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast> =
        weatherApi.getForecast(latitude, longitude).items.toDailyForecasts()
}
