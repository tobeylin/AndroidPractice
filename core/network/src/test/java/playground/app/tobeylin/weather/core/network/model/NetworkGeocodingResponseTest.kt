package playground.app.tobeylin.weather.core.network.model

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class NetworkGeocodingResponseTest {

    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    @Test
    fun geocoding_response_with_state_deserializes_correctly() {
        val jsonString = """[{"name":"London","lat":51.5085,"lon":-0.1257,"country":"GB","state":"England"}]"""
        val result = json.decodeFromString<List<NetworkGeocodingResponse>>(jsonString)
        assertEquals(1, result.size)
        assertEquals("London", result[0].name)
        assertEquals(51.5085, result[0].lat, 0.0001)
        assertEquals(-0.1257, result[0].lon, 0.0001)
        assertEquals("GB", result[0].country)
        assertEquals("England", result[0].state)
    }

    @Test
    fun geocoding_response_without_state_has_null_state() {
        val jsonString = """[{"name":"Tokyo","lat":35.6895,"lon":139.6917,"country":"JP"}]"""
        val result = json.decodeFromString<List<NetworkGeocodingResponse>>(jsonString)
        assertEquals(1, result.size)
        assertEquals("Tokyo", result[0].name)
        assertNull(result[0].state)
    }

    @Test
    fun geocoding_response_with_local_names_deserializes_correctly() {
        val jsonString = """[{"name":"London","local_names":{"zh":"伦敦","zh_tw":"倫敦","ja":"ロンドン","en":"London"},"lat":51.5085,"lon":-0.1257,"country":"GB"}]"""
        val result = json.decodeFromString<List<NetworkGeocodingResponse>>(jsonString)
        assertEquals(1, result.size)
        val localNames = result[0].localNames
        assertNotNull(localNames)
        assertEquals("倫敦", localNames!!["zh_tw"])
        assertEquals("伦敦", localNames["zh"])
        assertEquals("ロンドン", localNames["ja"])
        assertEquals("London", localNames["en"])
    }

    @Test
    fun geocoding_response_without_local_names_has_null() {
        val jsonString = """[{"name":"London","lat":51.5085,"lon":-0.1257,"country":"GB"}]"""
        val result = json.decodeFromString<List<NetworkGeocodingResponse>>(jsonString)
        assertNull(result[0].localNames)
    }
}
