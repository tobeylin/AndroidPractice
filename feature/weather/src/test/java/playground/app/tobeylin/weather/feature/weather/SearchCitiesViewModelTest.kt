package playground.app.tobeylin.weather.feature.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import playground.app.tobeylin.weather.core.model.City

@OptIn(ExperimentalCoroutinesApi::class)
class SearchCitiesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeCityRepository = FakeCityRepository()

    @Test
    fun initial_state_is_loading() {
        val pausedDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(pausedDispatcher)
        try {
            val viewModel = SearchCitiesViewModel(fakeCityRepository)
            assertEquals(SearchCitiesUiState.Loading, viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun when_cities_load_successfully_state_is_success() {
        fakeCityRepository.fakeCities = listOf(
            City("Taipei", "TW", 25.033, 121.565),
            City("Tokyo", "JP", 35.676, 139.650),
        )

        val viewModel = SearchCitiesViewModel(fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is SearchCitiesUiState.Success)

        val success = state as SearchCitiesUiState.Success
        assertEquals(2, success.cities.size)
        assertEquals("--", success.cities[0].temperature)
        assertEquals("", success.cities[0].condition)
        assertEquals("", success.cities[0].iconCode)
    }

    @Test
    fun all_cities_have_static_temperature_indicator() {
        fakeCityRepository.fakeCities = listOf(
            City("Taipei", "TW", 25.033, 121.565),
            City("Tokyo", "JP", 35.676, 139.650),
            City("London", "GB", 51.507, -0.128),
        )

        val viewModel = SearchCitiesViewModel(fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is SearchCitiesUiState.Success)

        val success = state as SearchCitiesUiState.Success
        success.cities.forEach { city ->
            assertEquals("--", city.temperature)
            assertEquals("", city.condition)
            assertEquals("", city.iconCode)
        }
    }

    @Test
    fun city_country_codes_are_converted_to_display_names() {
        fakeCityRepository.fakeCities = listOf(
            City("London", "GB", 51.507, -0.128),
        )

        val viewModel = SearchCitiesViewModel(fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is SearchCitiesUiState.Success)

        val success = state as SearchCitiesUiState.Success
        val city = success.cities[0]
        assertTrue(city.country.isNotEmpty())
        assertNotEquals("GB", city.country)
    }

    @Test
    fun when_cities_list_is_empty_state_is_success_with_empty_list() {
        fakeCityRepository.fakeCities = emptyList()

        val viewModel = SearchCitiesViewModel(fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is SearchCitiesUiState.Success)

        val success = state as SearchCitiesUiState.Success
        assertTrue(success.cities.isEmpty())
    }

    @Test
    fun cities_preserve_latitude_and_longitude() {
        fakeCityRepository.fakeCities = listOf(
            City("Tokyo", "JP", 35.676, 139.650),
        )

        val viewModel = SearchCitiesViewModel(fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is SearchCitiesUiState.Success)

        val success = state as SearchCitiesUiState.Success
        val city = success.cities[0]
        assertEquals(35.676, city.latitude, 0.001)
        assertEquals(139.650, city.longitude, 0.001)
    }
}
