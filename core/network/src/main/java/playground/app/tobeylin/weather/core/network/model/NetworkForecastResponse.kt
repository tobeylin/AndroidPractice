package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkForecastResponse(
    @SerialName("list") val items: List<NetworkForecastItem>,
    @SerialName("city") val city: NetworkCity,
)

@Serializable
data class NetworkForecastItem(
    @SerialName("dt") val dt: Long,
    @SerialName("main") val main: NetworkMain,
    @SerialName("weather") val weather: List<NetworkWeatherItem>,
    @SerialName("dt_txt") val dtTxt: String,
)

@Serializable
data class NetworkCity(
    @SerialName("name") val name: String,
    @SerialName("country") val country: String,
    @SerialName("timezone") val timezone: Int,
)
