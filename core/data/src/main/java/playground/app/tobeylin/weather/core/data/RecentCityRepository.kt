package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.flow.Flow
import playground.app.tobeylin.weather.core.model.City

interface RecentCityRepository {
    fun getRecentCities(): Flow<List<City>>
    suspend fun saveRecentCity(city: City)
}
