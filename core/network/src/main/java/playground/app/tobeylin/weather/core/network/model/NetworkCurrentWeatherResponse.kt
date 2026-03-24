package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkCurrentWeatherResponse(
    @SerialName("coord") val coord: NetworkCoord,
    @SerialName("weather") val weather: List<NetworkWeatherItem>,
    @SerialName("main") val main: NetworkMain,
    @SerialName("wind") val wind: NetworkWind,
    @SerialName("dt") val dt: Long,
    @SerialName("name") val name: String,
)

@Serializable
data class NetworkCoord(
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
)

@Serializable
data class NetworkWeatherItem(
    @SerialName("id") val id: Int,
    @SerialName("main") val main: String,
    @SerialName("description") val description: String,
    @SerialName("icon") val icon: String,
)

@Serializable
data class NetworkMain(
    @SerialName("temp") val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    @SerialName("humidity") val humidity: Int,
)

@Serializable
data class NetworkWind(
    @SerialName("speed") val speed: Double,
)
