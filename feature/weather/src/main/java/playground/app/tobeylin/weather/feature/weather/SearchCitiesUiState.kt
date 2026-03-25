package playground.app.tobeylin.weather.feature.weather

data class SearchCityItem(
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
)

sealed interface RecentCitiesUiState {
    data object Loading : RecentCitiesUiState
    data class Success(val cities: List<SearchCityItem>) : RecentCitiesUiState
    data object Empty : RecentCitiesUiState
}

sealed interface SearchResultsUiState {
    data object Idle : SearchResultsUiState
    data object Loading : SearchResultsUiState
    data class Success(val cities: List<SearchCityItem>) : SearchResultsUiState
    data class Empty(val query: String) : SearchResultsUiState
    data class Error(val message: String) : SearchResultsUiState
}
