package playground.app.tobeylin.weather.feature.weather

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import playground.app.tobeylin.weather.feature.weather.util.calculateDewPoint
import playground.app.tobeylin.weather.feature.weather.util.formatUnixTimestampToTime
import playground.app.tobeylin.weather.feature.weather.util.metersPerSecondToKmh
import playground.app.tobeylin.weather.feature.weather.util.windDegreesToCompass

class WeatherConversionsTest {

    @Test
    fun metersPerSecondToKmh_convertsCorrectly() {
        assertEquals(12.6, metersPerSecondToKmh(3.5), 0.01)
        assertEquals(0.0, metersPerSecondToKmh(0.0), 0.001)
        assertEquals(360.0, metersPerSecondToKmh(100.0), 0.01)
    }

    @Test
    fun windDegreesToCompass_returnsCorrectDirection() {
        assertEquals("N", windDegreesToCompass(0))
        assertEquals("NNE", windDegreesToCompass(22))
        assertEquals("NE", windDegreesToCompass(45))
        assertEquals("ENE", windDegreesToCompass(67))
        assertEquals("E", windDegreesToCompass(90))
        assertEquals("ESE", windDegreesToCompass(112))
        assertEquals("SE", windDegreesToCompass(135))
        assertEquals("SSE", windDegreesToCompass(157))
        assertEquals("S", windDegreesToCompass(180))
        assertEquals("SSW", windDegreesToCompass(202))
        assertEquals("SW", windDegreesToCompass(225))
        assertEquals("WSW", windDegreesToCompass(247))
        assertEquals("W", windDegreesToCompass(270))
        assertEquals("WNW", windDegreesToCompass(292))
        assertEquals("NW", windDegreesToCompass(315))
        assertEquals("NNW", windDegreesToCompass(337))
    }

    @Test
    fun windDegreesToCompass_handlesBoundaryWrap() {
        val deg348 = windDegreesToCompass(348)
        val deg349 = windDegreesToCompass(349)
        val deg360 = windDegreesToCompass(360)
        assertEquals("expected N for 348 but got $deg348", "NNW", deg348)
        assertEquals("expected N for 349 but got $deg349", "N", deg349)
        assertEquals("expected N for 360 but got $deg360", "N", deg360)
    }

    @Test
    fun windDegreesToCompass_handlesNull() {
        assertEquals("", windDegreesToCompass(null))
    }

    @Test
    fun calculateDewPoint_returnsCorrectValues() {
        assertEquals(22.6, calculateDewPoint(28.5, 70), 0.5)
        assertEquals(-9.3, calculateDewPoint(0.0, 50), 0.5)
    }

    @Test
    fun calculateDewPoint_handlesZeroHumidity() {
        val dewPoint = calculateDewPoint(25.0, 0)
        assertNotNull(dewPoint)
        assertEquals(true, dewPoint.isFinite())
    }

    @Test
    fun formatUnixTimestampToTime_formatsEpochZero() {
        val result = formatUnixTimestampToTime(0L)
        assertTrue("Expected AM or PM in '$result'", result.contains("AM") || result.contains("PM"))
    }

    @Test
    fun formatUnixTimestampToTime_formatsKnownTimestamp() {
        val result = formatUnixTimestampToTime(1711317120L)
        assertTrue("Expected AM or PM in '$result'", result.contains("AM") || result.contains("PM"))
    }
}
