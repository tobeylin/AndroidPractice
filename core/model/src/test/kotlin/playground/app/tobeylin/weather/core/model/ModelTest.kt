package playground.app.tobeylin.weather.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class ModelTest {

    @Test
    fun city_construction_and_equality() {
        val city1 = City("Taipei", "Taiwan", 25.0330, 121.5654)
        val city2 = City("Taipei", "Taiwan", 25.0330, 121.5654)
        val city3 = City("Tokyo", "Japan", 35.6762, 139.6503)

        assertEquals(city1, city2)
        assertNotEquals(city1, city3)
        assertEquals("Taipei", city1.name)
        assertEquals("Taiwan", city1.country)
        assertEquals(25.0330, city1.latitude, 0.0001)
        assertEquals(121.5654, city1.longitude, 0.0001)
    }

    @Test
    fun currentWeather_construction() {
        val weather = CurrentWeather(
            temperature = 25.5,
            feelsLike = 27.0,
            humidity = 80,
            windSpeed = 5.5,
            condition = "Clouds",
            conditionDescription = "broken clouds",
            conditionId = 804,
            tempMax = 28.0,
            tempMin = 22.0,
            timestamp = 1625097600L
        )

        assertEquals(25.5, weather.temperature, 0.0)
        assertEquals(27.0, weather.feelsLike, 0.0)
        assertEquals(80, weather.humidity)
        assertEquals(5.5, weather.windSpeed, 0.0)
        assertEquals("Clouds", weather.condition)
        assertEquals("broken clouds", weather.conditionDescription)
        assertEquals(804, weather.conditionId)
        assertEquals(1625097600L, weather.timestamp)
    }

    @Test
    fun dailyForecast_construction() {
        val forecast = DailyForecast(
            date = "2024-03-24",
            tempMax = 30.0,
            tempMin = 22.0,
            condition = "Clear",
            conditionDescription = "clear sky",
            conditionId = 800
        )

        assertEquals("2024-03-24", forecast.date)
        assertEquals(30.0, forecast.tempMax, 0.0)
        assertEquals(22.0, forecast.tempMin, 0.0)
        assertEquals("Clear", forecast.condition)
        assertEquals("clear sky", forecast.conditionDescription)
        assertEquals(800, forecast.conditionId)
    }

    @Test
    fun model_copy_functionality() {
        val city = City("Taipei", "Taiwan", 25.0, 121.0)
        val updatedCity = city.copy(name = "Kaohsiung")

        assertEquals("Kaohsiung", updatedCity.name)
        assertEquals("Taiwan", updatedCity.country)
        assertEquals(25.0, updatedCity.latitude, 0.0)
        assertEquals(121.0, updatedCity.longitude, 0.0)
    }
}
