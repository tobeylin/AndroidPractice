package playground.app.tobeylin.weather.core.model

data class DailyForecast(
    val date: String,
    val tempMax: Double,
    val tempMin: Double,
    val condition: String,
    val conditionDescription: String,
    val iconCode: String,
)
