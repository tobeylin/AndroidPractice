package playground.app.tobeylin.weather.feature.weather

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import playground.app.tobeylin.weather.core.data.FakeRecentCityRepository
import playground.app.tobeylin.weather.core.data.GeocodingRepository
import playground.app.tobeylin.weather.core.model.City

@OptIn(ExperimentalCoroutinesApi::class)
class SearchCitiesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var fakeRecentCityRepository: FakeRecentCityRepository
    private lateinit var fakeGeocodingRepository: FakeGeocodingRepository
    private lateinit var viewModel: SearchCitiesViewModel

    @Before
    fun setUp() {
        fakeRecentCityRepository = FakeRecentCityRepository()
        fakeGeocodingRepository = FakeGeocodingRepository()
        viewModel = SearchCitiesViewModel(fakeRecentCityRepository, fakeGeocodingRepository)
    }

    @Test
    fun initial_recentCitiesUiState_is_loading() = runTest {
        assertEquals(RecentCitiesUiState.Loading, viewModel.recentCitiesUiState.value)
    }

    @Test
    fun when_no_recent_cities_recentCitiesUiState_is_empty() = runTest {
        val state = viewModel.recentCitiesUiState.first { it !is RecentCitiesUiState.Loading }
        assertEquals(RecentCitiesUiState.Empty, state)
    }

    @Test
    fun when_recent_cities_exist_recentCitiesUiState_is_success() = runTest {
        fakeRecentCityRepository.saveRecentCity(City("London", "GB", 51.5085, -0.1257))
        val state = viewModel.recentCitiesUiState.first { it is RecentCitiesUiState.Success }
        val success = state as RecentCitiesUiState.Success
        assertEquals(1, success.cities.size)
        assertEquals("London", success.cities[0].name)
        assertEquals("GB", success.cities[0].country)
    }

    @Test
    fun initial_searchResultsUiState_is_idle() = runTest {
        val state = viewModel.searchResultsUiState.value
        assertEquals(SearchResultsUiState.Idle, state)
    }

    @Test
    fun after_debounce_query_returns_search_results() = runTest {
        val collectionJob = launch { viewModel.searchResultsUiState.collect {} }
        fakeGeocodingRepository.results = listOf(City("London", "GB", 51.5085, -0.1257))
        viewModel.onSearchQueryChange("London")
        advanceTimeBy(400)
        val state = viewModel.searchResultsUiState.value
        assertTrue(state is SearchResultsUiState.Success)
        val success = state as SearchResultsUiState.Success
        assertEquals(1, success.cities.size)
        assertEquals("London", success.cities[0].name)
        collectionJob.cancel()
    }

    @Test
    fun clear_search_resets_searchResultsUiState_to_idle() = runTest {
        val collectionJob = launch { viewModel.searchResultsUiState.collect {} }
        fakeGeocodingRepository.results = listOf(City("London", "GB", 51.5085, -0.1257))
        viewModel.onSearchQueryChange("London")
        advanceTimeBy(400)
        assertTrue(viewModel.searchResultsUiState.value is SearchResultsUiState.Success)
        viewModel.onClearSearch()
        advanceTimeBy(400)
        assertEquals(SearchResultsUiState.Idle, viewModel.searchResultsUiState.value)
        collectionJob.cancel()
    }

    @Test
    fun when_search_returns_empty_state_is_empty() = runTest {
        val collectionJob = launch { viewModel.searchResultsUiState.collect {} }
        fakeGeocodingRepository.results = emptyList()
        viewModel.onSearchQueryChange("Unknown City XYZ")
        advanceTimeBy(400)
        val state = viewModel.searchResultsUiState.value
        assertTrue(state is SearchResultsUiState.Empty)
        collectionJob.cancel()
    }

    @Test
    fun when_geocoding_throws_error_state_is_error() = runTest {
        val collectionJob = launch { viewModel.searchResultsUiState.collect {} }
        fakeGeocodingRepository.shouldThrow = true
        viewModel.onSearchQueryChange("London")
        advanceTimeBy(400)
        val state = viewModel.searchResultsUiState.value
        assertTrue(state is SearchResultsUiState.Error)
        collectionJob.cancel()
    }

    @Test
    fun rapid_queries_debounced_to_single_call() = runTest {
        val collectionJob = launch { viewModel.searchResultsUiState.collect {} }
        fakeGeocodingRepository.results = listOf(City("London", "GB", 51.5085, -0.1257))
        viewModel.onSearchQueryChange("L")
        viewModel.onSearchQueryChange("Lo")
        viewModel.onSearchQueryChange("Lon")
        viewModel.onSearchQueryChange("Lond")
        viewModel.onSearchQueryChange("Londo")
        viewModel.onSearchQueryChange("London")
        advanceTimeBy(400)
        assertEquals(1, fakeGeocodingRepository.callCount)
        collectionJob.cancel()
    }
}

private class FakeGeocodingRepository : GeocodingRepository {
    var results: List<City> = emptyList()
    var shouldThrow = false
    var callCount = 0

    override suspend fun searchCities(query: String): List<City> {
        callCount++
        if (shouldThrow) throw RuntimeException("Network error")
        return results
    }
}
