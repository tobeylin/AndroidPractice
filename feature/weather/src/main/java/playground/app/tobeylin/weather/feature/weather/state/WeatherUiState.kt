package playground.app.tobeylin.weather.feature.weather.state

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(
        val cityName: String,
        val temperature: Double,
        val tempMax: Double,
        val tempMin: Double,
        val condition: String,
        val conditionDescription: String,
        val conditionId: Int,
        val humidity: Int,
        val windSpeedKmh: Double,
        val windDirection: String,
        val dewPoint: Double,
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
