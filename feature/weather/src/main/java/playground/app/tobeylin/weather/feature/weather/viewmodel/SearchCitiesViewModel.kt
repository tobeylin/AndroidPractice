package playground.app.tobeylin.weather.feature.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import playground.app.tobeylin.weather.core.data.GeocodingRepository
import playground.app.tobeylin.weather.core.data.RecentCityRepository
import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.feature.weather.state.RecentCitiesUiState
import playground.app.tobeylin.weather.feature.weather.state.SearchCityItem
import playground.app.tobeylin.weather.feature.weather.state.SearchResultsUiState
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchCitiesViewModel @Inject constructor(
    private val recentCityRepository: RecentCityRepository,
    private val geocodingRepository: GeocodingRepository,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val recentCitiesUiState: StateFlow<RecentCitiesUiState> =
        recentCityRepository.getRecentCities()
            .map { cities ->
                if (cities.isEmpty()) RecentCitiesUiState.Empty
                else RecentCitiesUiState.Success(cities.map { it.toSearchCityItem() })
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RecentCitiesUiState.Loading)

    val searchResultsUiState: StateFlow<SearchResultsUiState> =
        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) SearchResultsUiState.Idle
                else {
                    try {
                        val results = geocodingRepository.searchCities(query)
                        if (results.isEmpty()) SearchResultsUiState.Empty(query)
                        else SearchResultsUiState.Success(results.map { it.toSearchCityItem() })
                    } catch (e: Exception) {
                        SearchResultsUiState.Error(e.message ?: "Search failed")
                    }
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SearchResultsUiState.Idle)

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onClearSearch() {
        _searchQuery.value = ""
    }
}

private fun City.toSearchCityItem() = SearchCityItem(
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
)
