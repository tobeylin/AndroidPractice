package playground.app.tobeylin.weather.feature.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import playground.app.tobeylin.weather.core.data.CityRepository
import playground.app.tobeylin.weather.core.data.WeatherRepository
import playground.app.tobeylin.weather.core.model.City
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _forecastUiState: MutableStateFlow<ForecastUiState> = MutableStateFlow(ForecastUiState.Loading)
    val forecastUiState: StateFlow<ForecastUiState> = _forecastUiState.asStateFlow()

    private var currentCity: City? = null

    fun retry() {
        viewModelScope.launch { loadAll() }
    }

    init {
        viewModelScope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        _uiState.value = WeatherUiState.Loading
        _forecastUiState.value = ForecastUiState.Loading
        val city = currentCity ?: cityRepository.getCities().firstOrNull()
        if (city == null) {
            _uiState.value = WeatherUiState.Error("No cities available")
            _forecastUiState.value = ForecastUiState.Error("No cities available")
            return
        }
        currentCity = city
        coroutineScope {
            launch { loadWeather(city) }
            launch { loadForecast(city) }
        }
    }

    private suspend fun loadWeather(city: City) {
        try {
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

    private suspend fun loadForecast(city: City) {
        try {
            val forecasts = weatherRepository.getDailyForecasts(city.latitude, city.longitude)
            val items = forecasts.map { forecast ->
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ENGLISH)
                val outputFormat = java.text.SimpleDateFormat("EEE", java.util.Locale.ENGLISH)
                val date = inputFormat.parse(forecast.date) ?: return@map DailyForecastItem(
                    dayOfWeek = "",
                    iconCode = forecast.iconCode,
                    condition = forecast.condition,
                    tempMax = "${forecast.tempMax.toInt()}°",
                    tempMin = "${forecast.tempMin.toInt()}°",
                )
                DailyForecastItem(
                    dayOfWeek = outputFormat.format(date),
                    iconCode = forecast.iconCode,
                    condition = forecast.condition,
                    tempMax = "${forecast.tempMax.toInt()}°",
                    tempMin = "${forecast.tempMin.toInt()}°",
                )
            }
            _forecastUiState.value = ForecastUiState.Success(items)
        } catch (e: Exception) {
            _forecastUiState.value = ForecastUiState.Error(e.message ?: "Unknown error")
        }
    }
}
