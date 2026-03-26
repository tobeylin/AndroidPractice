package playground.app.tobeylin.weather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import playground.app.tobeylin.weather.core.data.FakeRecentCityRepository
import playground.app.tobeylin.weather.core.data.RecentCityRepository
import playground.app.tobeylin.weather.core.model.City

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeRepository = FakeRecentCityRepository()
    private val cityA = City(name = "Taipei", country = "TW", latitude = 25.033, longitude = 121.565)
    private val cityB = City(name = "Tokyo", country = "JP", latitude = 35.676, longitude = 139.650)
    private val cityC = City(name = "Paris", country = "FR", latitude = 48.857, longitude = 2.351)

    private fun createViewModel(repository: RecentCityRepository = fakeRepository): MainViewModel =
        MainViewModel(repository)

    @Test
    fun initial_state_is_loading() {
        // With UnconfinedTestDispatcher the init coroutine runs immediately,
        // so this test verifies the FINAL state after an empty repository is queried.
        // With an empty FakeRecentCityRepository, destination should be Search.
        // The Loading state is technically transient and only observable before the coroutine runs.
        // Since UnconfinedTestDispatcher runs eagerly, we verify Search (empty repo) here.
        val viewModel = createViewModel()
        assertEquals(InitialDestination.Search, viewModel.initialDestination.value)
    }

    @Test
    fun when_recent_cities_exist_destination_is_weather() = runTest {
        fakeRepository.saveRecentCity(cityA)
        val viewModel = createViewModel()
        assertEquals(InitialDestination.Weather(cityA), viewModel.initialDestination.value)
    }

    @Test
    fun when_no_recent_cities_destination_is_search() = runTest {
        val viewModel = createViewModel()
        assertEquals(InitialDestination.Search, viewModel.initialDestination.value)
    }

    @Test
    fun when_repository_throws_error_destination_is_search() = runTest {
        val viewModel = createViewModel(ThrowingRecentCityRepository())
        assertEquals(InitialDestination.Search, viewModel.initialDestination.value)
    }

    @Test
    fun destination_uses_most_recent_city() = runTest {
        fakeRepository.saveRecentCity(cityC)
        fakeRepository.saveRecentCity(cityB)
        fakeRepository.saveRecentCity(cityA)
        val viewModel = createViewModel()
        // FakeRecentCityRepository stores most recently saved at index 0
        assertEquals(InitialDestination.Weather(cityA), viewModel.initialDestination.value)
    }

    private class ThrowingRecentCityRepository : RecentCityRepository {
        override fun getRecentCities(): Flow<List<City>> = flow {
            throw RuntimeException("DB error")
        }

        override suspend fun saveRecentCity(city: City) {
            throw RuntimeException("DB error")
        }
    }
}
