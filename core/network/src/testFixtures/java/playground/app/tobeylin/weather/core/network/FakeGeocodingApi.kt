package playground.app.tobeylin.weather.core.network

import playground.app.tobeylin.weather.core.network.model.NetworkGeocodingResponse

class FakeGeocodingApi : GeocodingApi {
    var results: List<NetworkGeocodingResponse> = emptyList()
    var shouldThrow: Boolean = false

    override suspend fun searchCities(query: String, limit: Int): List<NetworkGeocodingResponse> {
        if (shouldThrow) throw RuntimeException("Network error")
        return results
    }
}
