package playground.app.tobeylin.weather.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import playground.app.tobeylin.weather.core.data.CityRepository
import playground.app.tobeylin.weather.core.data.WeatherRepository
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        loadWeather()
    }

    private fun loadWeather() {
        viewModelScope.launch {
            try {
                val city = cityRepository.getCities().first()
                val weather = weatherRepository.getCurrentWeather(city.latitude, city.longitude)
                _uiState.value = WeatherUiState.Success(
                    cityName = city.name,
                    temperature = weather.temperature,
                    tempMax = weather.tempMax,
                    tempMin = weather.tempMin,
                    condition = weather.condition,
                    conditionDescription = weather.conditionDescription,
                    iconCode = weather.iconCode,
                    humidity = weather.humidity,
                    windSpeedKmh = metersPerSecondToKmh(weather.windSpeed),
                    windDirection = windDegreesToCompass(weather.windDeg),
                    dewPoint = calculateDewPoint(weather.temperature, weather.humidity),
                )
            } catch (e: Exception) {
                _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
