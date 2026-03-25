package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.core.network.GeocodingApi
import javax.inject.Inject

internal class DefaultGeocodingRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
) : GeocodingRepository {
    override suspend fun searchCities(query: String): List<City> =
        geocodingApi.searchCities(query = query).map { response ->
            City(
                name = response.name,
                country = response.country,
                latitude = response.lat,
                longitude = response.lon,
            )
        }
}
