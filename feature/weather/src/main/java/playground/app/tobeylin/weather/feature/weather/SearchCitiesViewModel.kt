package playground.app.tobeylin.weather.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import playground.app.tobeylin.weather.core.data.CityRepository
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SearchCitiesViewModel @Inject constructor(
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<SearchCitiesUiState> =
        MutableStateFlow(SearchCitiesUiState.Loading)
    val uiState: StateFlow<SearchCitiesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val cities = cityRepository.getCities()
                val items = cities.map { city ->
                    SearchCityItem(
                        name = city.name,
                        country = Locale("", city.country).displayCountry,
                        latitude = city.latitude,
                        longitude = city.longitude,
                        temperature = "--",
                        condition = "",
                        iconCode = "",
                    )
                }
                _uiState.value = SearchCitiesUiState.Success(items)
            } catch (e: Exception) {
                _uiState.value = SearchCitiesUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
