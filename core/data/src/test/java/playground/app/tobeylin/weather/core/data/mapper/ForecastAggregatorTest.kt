package playground.app.tobeylin.weather.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import playground.app.tobeylin.weather.core.network.model.NetworkForecastItem
import playground.app.tobeylin.weather.core.network.model.NetworkMain
import playground.app.tobeylin.weather.core.network.model.NetworkWeatherItem

class ForecastAggregatorTest {

    @Test
    fun empty_list_returns_empty_list() {
        val result = emptyList<NetworkForecastItem>().toDailyForecasts()

        assertTrue(result.isEmpty())
    }

    @Test
    fun single_item_maps_to_single_daily_forecast() {
        val item = makeItem(
            dtTxt = "2026-03-24 12:00:00",
            temp = 25.0,
            tempMin = 20.0,
            tempMax = 30.0,
            condition = "Clear",
            icon = "01d",
        )

        val result = listOf(item).toDailyForecasts()

        assertEquals(1, result.size)
        val dailyForecast = result.first()
        assertEquals("2026-03-24", dailyForecast.date)
        assertEquals(30.0, dailyForecast.tempMax, 0.001)
        assertEquals(20.0, dailyForecast.tempMin, 0.001)
        assertEquals("Clear", dailyForecast.condition)
        assertEquals("01d", dailyForecast.iconCode)
    }

    @Test
    fun full_day_with_eight_items_aggregates_min_max_and_condition() {
        val temps = listOf(28.0, 29.0, 30.0, 27.0, 25.0, 24.0, 26.0, 23.0)
        val items = temps.mapIndexed { index, temp ->
            val hour = (index * 3).toString().padStart(2, '0')
            makeItem(
                dtTxt = "2026-03-24 ${hour}:00:00",
                temp = temp,
                tempMin = temp,
                tempMax = temp,
                condition = "Clouds",
                icon = "03d",
            )
        }

        val result = items.toDailyForecasts()

        assertEquals(1, result.size)
        val dailyForecast = result.first()
        assertEquals(30.0, dailyForecast.tempMax, 0.001)
        assertEquals(23.0, dailyForecast.tempMin, 0.001)
        assertEquals("Clouds", dailyForecast.condition)
    }

    @Test
    fun partial_day_with_three_items_is_included_in_result() {
        val items = listOf(
            makeItem("2026-03-24 00:00:00", 25.0, 22.0, 28.0, "Rain", "10d"),
            makeItem("2026-03-24 03:00:00", 27.0, 24.0, 30.0, "Rain", "10d"),
            makeItem("2026-03-24 06:00:00", 24.0, 21.0, 29.0, "Rain", "10d"),
        )

        val result = items.toDailyForecasts()

        assertEquals(1, result.size)
        val dailyForecast = result.first()
        assertEquals(30.0, dailyForecast.tempMax, 0.001)
        assertEquals(21.0, dailyForecast.tempMin, 0.001)
        assertEquals("Rain", dailyForecast.condition)
    }

    @Test
    fun multiple_days_are_sorted_by_date_ascending() {
        val items = listOf(
            makeItem("2026-03-24 12:00:00", 25.0, 20.0, 30.0, "Clear", "01d"),
            makeItem("2026-03-25 12:00:00", 22.0, 18.0, 25.0, "Rain", "10d"),
            makeItem("2026-03-26 12:00:00", 23.0, 19.0, 27.0, "Clouds", "03d"),
        )

        val result = items.toDailyForecasts()

        assertEquals(3, result.size)
        assertEquals(listOf("2026-03-24", "2026-03-25", "2026-03-26"), result.map { it.date })
    }

    @Test
    fun dominant_condition_uses_majority_count() {
        val cloudsItems = (0 until 5).map { index ->
            val hour = (index * 3).toString().padStart(2, '0')
            makeItem("2026-03-24 ${hour}:00:00", 25.0, 25.0, 25.0, "Clouds", "03d")
        }
        val rainItems = (5 until 8).map { index ->
            val hour = (index * 3).toString().padStart(2, '0')
            makeItem("2026-03-24 ${hour}:00:00", 24.0, 24.0, 24.0, "Rain", "10d")
        }

        val result = (cloudsItems + rainItems).toDailyForecasts()

        assertEquals("Clouds", result.first().condition)
    }

    @Test
    fun dominant_condition_tie_breaker_prefers_first_seen_condition() {
        val cloudsItems = (0 until 4).map { index ->
            val hour = (index * 3).toString().padStart(2, '0')
            makeItem("2026-03-24 ${hour}:00:00", 25.0, 25.0, 25.0, "Clouds", "03d")
        }
        val rainItems = (4 until 8).map { index ->
            val hour = (index * 3).toString().padStart(2, '0')
            makeItem("2026-03-24 ${hour}:00:00", 24.0, 24.0, 24.0, "Rain", "10d")
        }

        val result = (cloudsItems + rainItems).toDailyForecasts()

        assertEquals("Clouds", result.first().condition)
    }

    private fun makeItem(
        dtTxt: String,
        temp: Double,
        tempMin: Double,
        tempMax: Double,
        condition: String,
        icon: String,
    ) = NetworkForecastItem(
        dt = 0L,
        main = NetworkMain(temp = temp, feelsLike = temp, tempMin = tempMin, tempMax = tempMax, humidity = 50),
        weather = listOf(
            NetworkWeatherItem(
                id = 0,
                main = condition,
                description = condition,
                icon = icon,
            ),
        ),
        dtTxt = dtTxt,
    )
}
