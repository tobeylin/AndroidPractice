package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.City
import javax.inject.Inject

interface CityRepository {
    fun getCities(): List<City>
}

internal class DefaultCityRepository @Inject constructor() : CityRepository {
    override fun getCities(): List<City> = CITIES

    companion object {
        private val CITIES = listOf(
            City("Taipei", "TW", 25.033, 121.565),
            City("Tokyo", "JP", 35.676, 139.650),
            City("Seoul", "KR", 37.566, 126.978),
            City("Bangkok", "TH", 13.756, 100.502),
            City("Singapore", "SG", 1.352, 103.820),
            City("Hong Kong", "HK", 22.319, 114.170),
            City("London", "GB", 51.507, -0.128),
            City("Paris", "FR", 48.857, 2.347),
            City("Berlin", "DE", 52.520, 13.405),
            City("Rome", "IT", 41.902, 12.496),
            City("New York", "US", 40.713, -74.006),
            City("Los Angeles", "US", 34.052, -118.244),
            City("São Paulo", "BR", -23.549, -46.633),
            City("Toronto", "CA", 43.651, -79.383),
            City("Sydney", "AU", -33.869, 151.209),
        )
    }
}
