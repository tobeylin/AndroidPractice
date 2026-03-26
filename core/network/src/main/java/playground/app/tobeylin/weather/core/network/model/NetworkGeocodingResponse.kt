package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkGeocodingResponse(
    @SerialName("name") val name: String,
    @SerialName("local_names") val localNames: Map<String, String>? = null,
    @SerialName("lat") val lat: Double,
    @SerialName("lon") val lon: Double,
    @SerialName("country") val country: String,
    @SerialName("state") val state: String? = null,
)
