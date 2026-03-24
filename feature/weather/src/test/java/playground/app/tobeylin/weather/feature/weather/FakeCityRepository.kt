package playground.app.tobeylin.weather.feature.weather

import playground.app.tobeylin.weather.core.data.CityRepository
import playground.app.tobeylin.weather.core.model.City

class FakeCityRepository : CityRepository {
    var cities: List<City> = listOf(
        City(name = "Taipei", country = "TW", latitude = 25.033, longitude = 121.565),
    )

    override fun getCities(): List<City> = cities
}
