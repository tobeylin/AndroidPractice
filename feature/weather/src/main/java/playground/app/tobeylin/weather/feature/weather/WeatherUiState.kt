package playground.app.tobeylin.weather.feature.weather

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(
        val cityName: String,
        val temperature: Double,
        val tempMax: Double,
        val tempMin: Double,
        val condition: String,
        val conditionDescription: String,
        val iconCode: String,
    ) : WeatherUiState
    data class Error(val message: String) : WeatherUiState
}
