package playground.app.tobeylin.weather.feature.weather

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import playground.app.tobeylin.weather.core.data.FakeWeatherRepository
import playground.app.tobeylin.weather.core.data.WeatherRepository
import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.core.model.CurrentWeather
import playground.app.tobeylin.weather.core.model.DailyForecast

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fakeWeatherRepository = FakeWeatherRepository()
    private val fakeCityRepository = FakeCityRepository()
    private val defaultCity = City(name = "Taipei", country = "TW", latitude = 25.033, longitude = 121.565)

    private fun createViewModel(weatherRepository: WeatherRepository = fakeWeatherRepository): WeatherViewModel =
        WeatherViewModel(weatherRepository, fakeCityRepository)

    @Test
    fun initial_state_is_loading() {
        val viewModel = createViewModel()
        assertEquals(WeatherUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun when_weather_loads_successfully_state_is_success() {
        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)

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

        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Error)
    }

    @Test
    fun forecast_initial_state_is_loading() {
        val viewModel = createViewModel()
        assertEquals(ForecastUiState.Loading, viewModel.forecastUiState.value)
    }

    @Test
    fun when_forecast_loads_successfully_state_is_success() {
        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)

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

        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)

        val state = viewModel.forecastUiState.value
        assertTrue(state is ForecastUiState.Error)
    }

    @Test
    fun when_forecast_fails_current_weather_still_loads() {
        fakeWeatherRepository.shouldThrowForecastError = true

        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)

        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
        assertTrue(viewModel.forecastUiState.value is ForecastUiState.Error)
    }

    @Test
    fun retry_after_error_loads_successfully() {
        fakeWeatherRepository.shouldThrowError = true
        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)

        fakeWeatherRepository.shouldThrowError = false
        viewModel.retry()
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)
    }

    @Test
    fun retry_when_still_failing_stays_error() {
        fakeWeatherRepository.shouldThrowError = true
        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)
        viewModel.retry()
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)
    }

    @Test
    fun when_weather_throws_exception_state_is_error() {
        fakeWeatherRepository.shouldThrowError = true
        val viewModel = createViewModel()
        viewModel.loadCity(defaultCity)
        assertTrue(viewModel.uiState.value is WeatherUiState.Error)
        val error = viewModel.uiState.value as WeatherUiState.Error
        assertTrue(error.message.isNotEmpty())
    }

    @Test
    fun loadCity_loads_weather_for_specified_city() {
        val viewModel = createViewModel()
        val tokyo = City(name = "Tokyo", country = "JP", latitude = 35.676, longitude = 139.650)

        viewModel.loadCity(tokyo)

        val state = viewModel.uiState.value
        assertTrue(state is WeatherUiState.Success)
        val success = state as WeatherUiState.Success
        assertEquals("Tokyo", success.cityName)
    }

    @Test
    fun loadCity_resets_state_to_loading() {
        val delayableRepository = DelayableWeatherRepository(fakeWeatherRepository)
        val viewModel = createViewModel(delayableRepository)
        val tokyo = City(name = "Tokyo", country = "JP", latitude = 35.676, longitude = 139.650)

        viewModel.loadCity(defaultCity)
        assertTrue(viewModel.uiState.value is WeatherUiState.Success)

        delayableRepository.blockRequests()
        viewModel.loadCity(tokyo)

        assertEquals(WeatherUiState.Loading, viewModel.uiState.value)
        delayableRepository.unblockRequests()
    }

    private class DelayableWeatherRepository(
        private val delegate: FakeWeatherRepository,
    ) : WeatherRepository {
        private var gate: CompletableDeferred<Unit>? = null

        fun blockRequests() {
            gate = CompletableDeferred()
        }

        fun unblockRequests() {
            gate?.complete(Unit)
            gate = null
        }

        override suspend fun getCurrentWeather(latitude: Double, longitude: Double): CurrentWeather {
            gate?.await()
            return delegate.getCurrentWeather(latitude, longitude)
        }

        override suspend fun getDailyForecasts(latitude: Double, longitude: Double): List<DailyForecast> {
            gate?.await()
            return delegate.getDailyForecasts(latitude, longitude)
        }
    }
}
