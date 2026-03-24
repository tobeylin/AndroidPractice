package playground.app.tobeylin.weather.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import playground.app.tobeylin.weather.core.network.model.NetworkCoord
import playground.app.tobeylin.weather.core.network.model.NetworkCurrentWeatherResponse
import playground.app.tobeylin.weather.core.network.model.NetworkMain
import playground.app.tobeylin.weather.core.network.model.NetworkWeatherItem
import playground.app.tobeylin.weather.core.network.model.NetworkWind

class WeatherMapperTest {

    @Test
    fun complete_mapping_maps_all_fields_correctly() {
        val response = NetworkCurrentWeatherResponse(
            coord = NetworkCoord(lat = 25.0, lon = 121.5),
            weather = listOf(
                NetworkWeatherItem(
                    id = 802,
                    main = "Clouds",
                    description = "scattered clouds",
                    icon = "03d",
                ),
            ),
            main = NetworkMain(
                temp = 28.5,
                feelsLike = 31.2,
                tempMin = 27.0,
                tempMax = 30.0,
                humidity = 70,
            ),
            wind = NetworkWind(speed = 3.5, deg = 180),
            dt = 1711296000L,
            name = "Taipei",
        )

        val result = response.asExternalModel()

        assertEquals(28.5, result.temperature, 0.001)
        assertEquals(30.0, result.tempMax, 0.001)
        assertEquals(27.0, result.tempMin, 0.001)
        assertEquals(31.2, result.feelsLike, 0.001)
        assertEquals(70, result.humidity)
        assertEquals(3.5, result.windSpeed, 0.001)
        assertEquals(180, result.windDeg)
        assertEquals("Clouds", result.condition)
        assertEquals("scattered clouds", result.conditionDescription)
        assertEquals("03d", result.iconCode)
        assertEquals(1711296000L, result.timestamp)
    }

    @Test
    fun empty_weather_list_returns_default_values() {
        val response = NetworkCurrentWeatherResponse(
            coord = NetworkCoord(lat = 0.0, lon = 0.0),
            weather = emptyList(),
            main = NetworkMain(
                temp = 20.0,
                feelsLike = 19.0,
                tempMin = 18.0,
                tempMax = 22.0,
                humidity = 50,
            ),
            wind = NetworkWind(speed = 1.0),
            dt = 1711300000L,
            name = "Unknown City",
        )

        val result = response.asExternalModel()

        assertEquals("Unknown", result.condition)
        assertEquals("", result.conditionDescription)
        assertEquals("", result.iconCode)
    }

    @Test
    fun multiple_weather_items_uses_first_item() {
        val firstItem = NetworkWeatherItem(
            id = 500,
            main = "Rain",
            description = "light rain",
            icon = "10d",
        )
        val secondItem = NetworkWeatherItem(
            id = 802,
            main = "Clouds",
            description = "overcast clouds",
            icon = "04d",
        )
        val response = NetworkCurrentWeatherResponse(
            coord = NetworkCoord(lat = 35.0, lon = 139.0),
            weather = listOf(firstItem, secondItem),
            main = NetworkMain(
                temp = 15.0,
                feelsLike = 13.0,
                tempMin = 12.0,
                tempMax = 17.0,
                humidity = 85,
            ),
            wind = NetworkWind(speed = 5.0),
            dt = 1711310000L,
            name = "Tokyo",
        )

        val result = response.asExternalModel()

        assertEquals("Rain", result.condition)
        assertEquals("light rain", result.conditionDescription)
        assertEquals("10d", result.iconCode)
    }

    @Test
    fun null_wind_deg_is_mapped_to_null() {
        val response = NetworkCurrentWeatherResponse(
            coord = NetworkCoord(lat = 0.0, lon = 0.0),
            weather = emptyList(),
            main = NetworkMain(
                temp = 20.0,
                feelsLike = 19.0,
                tempMin = 18.0,
                tempMax = 22.0,
                humidity = 50,
            ),
            wind = NetworkWind(speed = 1.0, deg = null),
            dt = 1711300000L,
            name = "Unknown City",
        )

        val result = response.asExternalModel()

        assertEquals(null, result.windDeg)
    }
}
