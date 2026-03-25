package playground.app.tobeylin.weather.core.network

import playground.app.tobeylin.weather.core.network.model.NetworkGeocodingResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET("geo/1.0/direct")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("limit") limit: Int = 5,
    ): List<NetworkGeocodingResponse>
}
