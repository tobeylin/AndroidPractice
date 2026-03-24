package playground.app.tobeylin.weather.core.data.mapper

import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.network.model.NetworkCurrentWeatherResponse

internal fun NetworkCurrentWeatherResponse.asExternalModel(): CurrentWeather = CurrentWeather(
    temperature = main.temp,
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    windSpeed = wind.speed,
    condition = weather.firstOrNull()?.main ?: "Unknown",
    conditionDescription = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "",
    timestamp = dt,
)
