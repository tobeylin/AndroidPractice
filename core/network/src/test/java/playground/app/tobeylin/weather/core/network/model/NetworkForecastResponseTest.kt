package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NetworkForecastResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun deserialize_forecastResponse_allFieldsMapped() {
        val jsonString = """
            {"list":[{"dt":1711296000,"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"weather":[{"id":802,"main":"Clouds","description":"scattered clouds","icon":"03d"}],"dt_txt":"2026-03-24 12:00:00"},{"dt":1711306800,"main":{"temp":27.0,"feels_like":29.5,"temp_min":26.0,"temp_max":28.0,"humidity":75},"weather":[{"id":500,"main":"Rain","description":"light rain","icon":"10d"}],"dt_txt":"2026-03-24 15:00:00"}],"city":{"name":"Taipei","country":"TW","timezone":28800}}
        """.trimIndent()

        val result = json.decodeFromString<NetworkForecastResponse>(jsonString)

        assertEquals(2, result.items.size)
        assertEquals("2026-03-24 12:00:00", result.items[0].dtTxt)
        assertEquals(28.5, result.items[0].main.temp, 0.001)
        assertEquals("Rain", result.items[1].weather[0].main)
        assertEquals("Taipei", result.city.name)
        assertEquals(28800, result.city.timezone)
    }

    @Test
    fun deserialize_itemWithEmptyWeatherList_noException() {
        val jsonString = """
            {"list":[{"dt":1711296000,"main":{"temp":28.5,"feels_like":31.2,"temp_min":27.0,"temp_max":30.0,"humidity":70},"weather":[],"dt_txt":"2026-03-24 12:00:00"}],"city":{"name":"Taipei","country":"TW","timezone":28800}}
        """.trimIndent()

        val result = json.decodeFromString<NetworkForecastResponse>(jsonString)

        assertTrue(result.items[0].weather.isEmpty())
    }
}
