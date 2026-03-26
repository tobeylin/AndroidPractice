package playground.app.tobeylin.weather.feature.weather.state

sealed interface ForecastUiState {
    data object Loading : ForecastUiState
    data class Success(val items: List<DailyForecastItem>) : ForecastUiState
    data class Error(val message: String) : ForecastUiState
}

data class DailyForecastItem(
    val dayOfWeek: String,
    val conditionId: Int,
    val condition: String,
    val tempMax: String,
    val tempMin: String,
)
