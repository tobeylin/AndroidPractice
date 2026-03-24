package playground.app.tobeylin.weather.core.network

import playground.app.tobeylin.weather.core.network.model.NetworkCity
import playground.app.tobeylin.weather.core.network.model.NetworkCoord
import playground.app.tobeylin.weather.core.network.model.NetworkCurrentWeatherResponse
import playground.app.tobeylin.weather.core.network.model.NetworkForecastItem
import playground.app.tobeylin.weather.core.network.model.NetworkForecastResponse
import playground.app.tobeylin.weather.core.network.model.NetworkMain
import playground.app.tobeylin.weather.core.network.model.NetworkWeatherItem
import playground.app.tobeylin.weather.core.network.model.NetworkWind
import java.io.IOException

class FakeWeatherApi : WeatherApi {

    var shouldThrowError = false

    override suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double,
        units: String,
    ): NetworkCurrentWeatherResponse {
        if (shouldThrowError) throw IOException("Fake network error")
        return NetworkCurrentWeatherResponse(
            coord = NetworkCoord(lat = latitude, lon = longitude),
            weather = listOf(
                NetworkWeatherItem(id = 802, main = "Clouds", description = "scattered clouds", icon = "03d"),
            ),
            main = NetworkMain(temp = 28.5, feelsLike = 31.2, tempMin = 27.0, tempMax = 30.0, humidity = 70),
            wind = NetworkWind(speed = 3.5, deg = 180),
            dt = 1711296000L,
            name = "Taipei",
        )
    }

    override suspend fun getForecast(
        latitude: Double,
        longitude: Double,
        units: String,
    ): NetworkForecastResponse {
        if (shouldThrowError) throw IOException("Fake network error")
        return NetworkForecastResponse(
            items = listOf(
                NetworkForecastItem(
                    dt = 1711296000L,
                    main = NetworkMain(temp = 28.5, feelsLike = 31.2, tempMin = 27.0, tempMax = 30.0, humidity = 70),
                    weather = listOf(NetworkWeatherItem(id = 802, main = "Clouds", description = "scattered clouds", icon = "03d")),
                    dtTxt = "2026-03-24 12:00:00",
                ),
                NetworkForecastItem(
                    dt = 1711382400L,
                    main = NetworkMain(temp = 25.0, feelsLike = 27.0, tempMin = 24.0, tempMax = 26.0, humidity = 75),
                    weather = listOf(NetworkWeatherItem(id = 500, main = "Rain", description = "light rain", icon = "10d")),
                    dtTxt = "2026-03-25 12:00:00",
                ),
            ),
            city = NetworkCity(name = "Taipei", country = "TW", timezone = 28800),
        )
    }
}
