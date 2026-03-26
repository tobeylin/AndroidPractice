package playground.app.tobeylin.weather.core.data.mapper

import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.network.model.NetworkCurrentWeatherResponse

internal fun NetworkCurrentWeatherResponse.asExternalModel(): CurrentWeather = CurrentWeather(
    temperature = main.temp,
    tempMax = main.tempMax,
    tempMin = main.tempMin,
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    windSpeed = wind.speed,
    windDeg = wind.deg,
    condition = weather.firstOrNull()?.main ?: "Unknown",
    conditionDescription = weather.firstOrNull()?.description ?: "",
    conditionId = weather.firstOrNull()?.id ?: 0,
    timestamp = dt,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
)
