package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import playground.app.tobeylin.weather.core.database.dao.RecentCityDao
import playground.app.tobeylin.weather.core.database.model.RecentCityEntity
import playground.app.tobeylin.weather.core.model.City

class DefaultRecentCityRepositoryTest {

    private lateinit var fakeDao: FakeRecentCityDao
    private lateinit var repository: DefaultRecentCityRepository

    @Before
    fun setUp() {
        fakeDao = FakeRecentCityDao()
        repository = DefaultRecentCityRepository(fakeDao)
    }

    @Test
    fun getRecentCities_returns_empty_initially() = runTest {
        val result = repository.getRecentCities().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun saveRecentCity_then_getRecentCities_returns_saved_city() = runTest {
        val city = City("London", "GB", 51.5085, -0.1257)
        repository.saveRecentCity(city)
        val result = repository.getRecentCities().first()
        assertEquals(1, result.size)
        assertEquals("London", result[0].name)
    }

    @Test
    fun saving_same_city_twice_does_not_create_duplicate() = runTest {
        val city = City("London", "GB", 51.5085, -0.1257)
        repository.saveRecentCity(city)
        repository.saveRecentCity(city)
        val result = repository.getRecentCities().first()
        assertEquals(1, result.size)
    }

    @Test
    fun saving_four_cities_keeps_only_three_most_recent() = runTest {
        repository.saveRecentCity(City("Tokyo", "JP", 35.6895, 139.6917))
        repository.saveRecentCity(City("Paris", "FR", 48.8566, 2.3522))
        repository.saveRecentCity(City("London", "GB", 51.5085, -0.1257))
        repository.saveRecentCity(City("Berlin", "DE", 52.5200, 13.4050))
        // "Tokyo" should be pruned (oldest)
        val result = repository.getRecentCities().first()
        assertEquals(3, result.size)
        assertTrue(result.none { it.name == "Tokyo" })
    }
}

private class FakeRecentCityDao : RecentCityDao {
    private val _entities = MutableStateFlow<List<RecentCityEntity>>(emptyList())
    private val insertionOrder = mutableMapOf<String, Long>()
    private var insertionCounter = 0L

    override fun getRecentCities(limit: Int): Flow<List<RecentCityEntity>> =
        _entities.map { list ->
            list.sortedWith(compareByDescending<RecentCityEntity> { it.viewedAt }
                .thenByDescending { insertionOrder[it.id] ?: 0L })
                .take(limit)
        }

    override suspend fun upsertCity(city: RecentCityEntity) {
        val current = _entities.value.toMutableList()
        current.removeIf { it.id == city.id }
        current.add(city)
        insertionOrder[city.id] = insertionCounter++
        _entities.value = current
    }

    override suspend fun pruneOldEntries(keepCount: Int) {
        _entities.value = _entities.value
            .sortedWith(compareByDescending<RecentCityEntity> { it.viewedAt }
                .thenByDescending { insertionOrder[it.id] ?: 0L })
            .take(keepCount)
    }
}
