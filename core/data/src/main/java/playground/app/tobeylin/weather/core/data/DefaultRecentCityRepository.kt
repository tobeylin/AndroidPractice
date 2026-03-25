package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import playground.app.tobeylin.weather.core.database.dao.RecentCityDao
import playground.app.tobeylin.weather.core.database.model.RecentCityEntity
import playground.app.tobeylin.weather.core.model.City
import javax.inject.Inject

internal class DefaultRecentCityRepository @Inject constructor(
    private val recentCityDao: RecentCityDao,
) : RecentCityRepository {

    override fun getRecentCities(): Flow<List<City>> =
        recentCityDao.getRecentCities(limit = 3).map { entities ->
            entities.map { entity ->
                City(
                    name = entity.name,
                    country = entity.country,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                )
            }
        }

    override suspend fun saveRecentCity(city: City) {
        val entity = RecentCityEntity(
            id = "${city.name}_${city.country}",
            name = city.name,
            country = city.country,
            latitude = city.latitude,
            longitude = city.longitude,
            viewedAt = System.currentTimeMillis(),
        )
        recentCityDao.upsertCity(entity)
        recentCityDao.pruneOldEntries(keepCount = 3)
    }
}
