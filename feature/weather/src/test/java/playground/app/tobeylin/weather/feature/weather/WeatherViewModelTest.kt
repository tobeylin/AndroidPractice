package playground.app.tobeylin.weather.feature.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import playground.app.tobeylin.weather.core.data.FakeWeatherRepository

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeWeatherRepository = FakeWeatherRepository()
    private val fakeCityRepository = FakeCityRepository()

    @Test
    fun initial_state_is_loading() {
        // Use a paused dispatcher so the init coroutine does NOT run eagerly
        val pausedDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(pausedDispatcher)
        try {
            val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)
            // Before advancing the dispatcher, state should still be Loading
            assertEquals(WeatherUiState.Loading, viewModel.uiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun when_weather_loads_successfully_state_is_success() {
        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)

        val success = state as WeatherUiState.Success
        assertEquals("Taipei", success.cityName)
        assertEquals(28.5, success.temperature, 0.01)
        assertEquals(32.0, success.tempMax, 0.01)
        assertEquals(25.0, success.tempMin, 0.01)
        assertEquals("Clouds", success.condition)
        assertEquals("03d", success.iconCode)
        assertEquals(70, success.humidity)
        assertEquals(12.6, success.windSpeedKmh, 0.1)
        assertEquals("S", success.windDirection)
        assertEquals(22.6, success.dewPoint, 0.5)
    }

    @Test
    fun when_weather_load_fails_state_is_error() {
        fakeWeatherRepository.shouldThrowError = true

        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
    }

    @Test
    fun forecast_initial_state_is_loading() {
        val pausedDispatcher = StandardTestDispatcher()
        Dispatchers.setMain(pausedDispatcher)
        try {
            val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)
            assertEquals(ForecastUiState.Loading, viewModel.forecastUiState.value)
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun when_forecast_loads_successfully_state_is_success() {
        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)

        val state = viewModel.forecastUiState.value
        assertTrue(state is ForecastUiState.Success)

        val success = state as ForecastUiState.Success
        assertEquals(2, success.items.size)
        assertEquals("Tue", success.items[0].dayOfWeek)
        assertEquals("30°", success.items[0].tempMax)
        assertEquals("27°", success.items[0].tempMin)
        assertEquals("Wed", success.items[1].dayOfWeek)
        assertEquals("Rain", success.items[1].condition)
    }

    @Test
    fun when_forecast_load_fails_state_is_error() {
        fakeWeatherRepository.shouldThrowForecastError = true

        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)

        val state = viewModel.forecastUiState.value
        assertTrue(state is ForecastUiState.Error)
    }

    @Test
    fun when_forecast_fails_current_weather_still_loads() {
        fakeWeatherRepository.shouldThrowForecastError = true

        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)

        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
        assertTrue(viewModel.forecastUiState.value is ForecastUiState.Error)
    }

    @Test
    fun retry_after_error_loads_successfully() {
        fakeWeatherRepository.shouldThrowError = true
        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)

        fakeWeatherRepository.shouldThrowError = false
        viewModel.retry()
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
    }

    @Test
    fun retry_when_still_failing_stays_error() {
        fakeWeatherRepository.shouldThrowError = true
        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)
        viewModel.retry()
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)
    }

    @Test
    fun empty_cities_list_shows_error() {
        fakeCityRepository.fakeCities = emptyList()
        val viewModel = WeatherViewModel(fakeWeatherRepository, fakeCityRepository)
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)
        val error = viewModel.uiState.value as WeatherUiState.Error
        assertTrue(error.message.isNotEmpty())
    }
}
