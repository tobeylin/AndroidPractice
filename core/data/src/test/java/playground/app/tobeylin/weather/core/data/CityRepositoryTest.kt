package playground.app.tobeylin.weather.core.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CityRepositoryTest {

    private val repository = DefaultCityRepository()

    @Test
    fun getCities_returnsNonEmptyList() {
        val cities = repository.getCities()
        assertTrue(cities.isNotEmpty())
        assertTrue(cities.size >= 15)
    }

    @Test
    fun getCities_allCitiesHaveValidCoordinates() {
        val cities = repository.getCities()
        cities.forEach { city ->
            assertTrue("${city.name} lat out of range", city.latitude in -90.0..90.0)
            assertTrue("${city.name} lon out of range", city.longitude in -180.0..180.0)
        }
    }

    @Test
    fun getCities_noDuplicateCityNames() {
        val cities = repository.getCities()
        val names = cities.map { it.name }
        assertEquals(names.size, names.distinct().size)
    }
}
