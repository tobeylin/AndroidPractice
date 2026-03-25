package playground.app.tobeylin.weather.core.data

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import playground.app.tobeylin.weather.core.network.FakeGeocodingApi
import playground.app.tobeylin.weather.core.network.model.NetworkGeocodingResponse

class DefaultGeocodingRepositoryTest {

    private lateinit var fakeApi: FakeGeocodingApi
    private lateinit var repository: DefaultGeocodingRepository

    @Before
    fun setUp() {
        fakeApi = FakeGeocodingApi()
        repository = DefaultGeocodingRepository(fakeApi)
    }

    @Test
    fun searchCities_returns_mapped_city_list() = runTest {
        fakeApi.results = listOf(
            NetworkGeocodingResponse(name = "London", lat = 51.5085, lon = -0.1257, country = "GB", state = "England"),
        )
        val result = repository.searchCities("London")
        assertEquals(1, result.size)
        assertEquals("London", result[0].name)
        assertEquals("GB", result[0].country)
        assertEquals(51.5085, result[0].latitude, 0.0001)
        assertEquals(-0.1257, result[0].longitude, 0.0001)
    }

    @Test
    fun searchCities_returns_empty_list_when_no_results() = runTest {
        fakeApi.results = emptyList()
        val result = repository.searchCities("xyznotacity")
        assertTrue(result.isEmpty())
    }

    @Test(expected = RuntimeException::class)
    fun searchCities_propagates_api_exception() = runTest {
        fakeApi.shouldThrow = true
        repository.searchCities("London")
    }
}
