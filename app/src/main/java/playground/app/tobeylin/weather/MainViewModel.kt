package playground.app.tobeylin.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import playground.app.tobeylin.weather.core.data.RecentCityRepository
import playground.app.tobeylin.weather.core.model.City
import javax.inject.Inject

sealed interface InitialDestination {
    data object Loading : InitialDestination
    data object Search : InitialDestination
    data class Weather(val city: City) : InitialDestination
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val recentCityRepository: RecentCityRepository,
) : ViewModel() {

    private val _initialDestination = MutableStateFlow<InitialDestination>(InitialDestination.Loading)
    val initialDestination: StateFlow<InitialDestination> = _initialDestination.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                val cities = recentCityRepository.getRecentCities().first()
                _initialDestination.value = if (cities.isEmpty()) {
                    InitialDestination.Search
                } else {
                    InitialDestination.Weather(cities.first())
                }
            } catch (e: Exception) {
                _initialDestination.value = InitialDestination.Search
            }
        }
    }
}
