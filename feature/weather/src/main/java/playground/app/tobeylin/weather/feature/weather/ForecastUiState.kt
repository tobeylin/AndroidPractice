package playground.app.tobeylin.weather.feature.weather

sealed interface ForecastUiState {
    data object Loading : ForecastUiState
    data class Success(val items: List<DailyForecastItem>) : ForecastUiState
    data class Error(val message: String) : ForecastUiState
}

data class DailyForecastItem(
    val dayOfWeek: String,
    val iconCode: String,
    val condition: String,
    val tempMax: String,
    val tempMin: String,
)
