package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.City

interface GeocodingRepository {
    suspend fun searchCities(query: String): List<City>
}
