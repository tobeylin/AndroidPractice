package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class NetworkCurrentWeatherResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun deserialize_currentWeatherResponse_allFieldsMapped() {
        val jsonString = """
            {"coord":{"lon":121.5654,"lat":25.033},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"wind":{"speed":3.5},"dt":1711296000,"name":"Taipei"}
        """.trimIndent()

        val result = json.decodeFromString<NetworkCurrentWeatherResponse>(jsonString)

        assertEquals("Taipei", result.name)
        assertEquals(28.5, result.main.temp, 0.001)
        assertEquals(70, result.main.humidity)
        assertEquals("Clouds", result.weather[0].main)
        assertEquals("03d", result.weather[0].icon)
        assertEquals(3.5, result.wind.speed, 0.001)
        assertEquals(1711296000L, result.dt)
        assertEquals(25.033, result.coord.lat, 0.001)
    }

    @Test
    fun deserialize_withUnknownFields_ignoredSuccessfully() {
        val jsonString = """
            {"coord":{"lon":121.5654,"lat":25.033},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"wind":{"speed":3.5},"visibility":10000,"dt":1711296000,"name":"Taipei"}
        """.trimIndent()

        val result = json.decodeFromString<NetworkCurrentWeatherResponse>(jsonString)

        assertEquals("Taipei", result.name)
        assertEquals(28.5, result.main.temp, 0.001)
    }
}
