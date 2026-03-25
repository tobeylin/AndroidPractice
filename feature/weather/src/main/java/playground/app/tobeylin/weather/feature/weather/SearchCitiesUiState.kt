package playground.app.tobeylin.weather.feature.weather

data class SearchCityItem(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: String,
    val condition: String,
    val iconCode: String,
)

sealed interface SearchCitiesUiState {
    data object Loading : SearchCitiesUiState
    data class Success(val cities: List<SearchCityItem>) : SearchCitiesUiState
    data class Error(val message: String) : SearchCitiesUiState
}
