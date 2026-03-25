package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import playground.app.tobeylin.weather.core.model.City

class FakeRecentCityRepository : RecentCityRepository {

    private val _cities = MutableStateFlow<List<City>>(emptyList())

    override fun getRecentCities(): Flow<List<City>> = _cities.asStateFlow()

    override suspend fun saveRecentCity(city: City) {
        val current = _cities.value.toMutableList()
        current.removeIf { it.name == city.name && it.country == city.country }
        current.add(0, city)
        _cities.value = current.take(3)
    }
}
