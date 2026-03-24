package playground.app.tobeylin.weather.core.model

data class CurrentWeather(
    val temperature: Double,
    val tempMax: Double,
    val tempMin: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val windDeg: Int? = null,
    val condition: String,
    val conditionDescription: String,
    val iconCode: String,
    val timestamp: Long,
)
