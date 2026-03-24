package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import playground.app.tobeylin.weather.core.network.FakeWeatherApi
import java.io.IOException

class DefaultWeatherRepositoryTest {

    private lateinit var fakeWeatherApi: FakeWeatherApi
    private lateinit var repository: DefaultWeatherRepository

    @Before
    fun setUp() {
        fakeWeatherApi = FakeWeatherApi()
        repository = DefaultWeatherRepository(weatherApi = fakeWeatherApi)
    }

    @Test
    fun getCurrentWeather_returnsCorrectTemperature() = runTest {
        val result = repository.getCurrentWeather(latitude = 25.033, longitude = 121.565)

        assertEquals(28.5, result.temperature, 0.01)
    }

    @Test
    fun getDailyForecasts_returnsList() = runTest {
        val result = repository.getDailyForecasts(latitude = 25.033, longitude = 121.565)

        assertTrue(result.isNotEmpty())
        assertEquals(2, result.size)
    }

    @Test
    fun getCurrentWeather_whenError_throwsIOException() = runTest {
        fakeWeatherApi.shouldThrowError = true

        try {
            repository.getCurrentWeather(latitude = 25.033, longitude = 121.565)
            fail("Expected IOException to be thrown")
        } catch (_: IOException) {
        }
    }
}
