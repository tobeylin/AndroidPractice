package playground.app.tobeylin.weather.core.database.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RecentCityEntityTest {

    @Test
    fun entity_id_is_composed_from_name_and_country() {
        val entity = RecentCityEntity(
            id = "London_GB",
            name = "London",
            country = "GB",
            latitude = 51.5085,
            longitude = -0.1257,
            viewedAt = 1000L,
        )
        assertEquals("London_GB", entity.id)
        assertEquals("London", entity.name)
        assertEquals("GB", entity.country)
    }

    @Test
    fun entity_fields_map_correctly_to_city_model_fields() {
        val entity = RecentCityEntity(
            id = "Tokyo_JP",
            name = "Tokyo",
            country = "JP",
            latitude = 35.6895,
            longitude = 139.6917,
            viewedAt = 2000L,
        )
        assertEquals(35.6895, entity.latitude, 0.0001)
        assertEquals(139.6917, entity.longitude, 0.0001)
        assertEquals(2000L, entity.viewedAt)
    }
}
