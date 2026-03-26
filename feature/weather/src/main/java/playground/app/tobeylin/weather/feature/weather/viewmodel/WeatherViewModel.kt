package playground.app.tobeylin.weather.feature.weather.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import playground.app.tobeylin.weather.core.data.CityRepository
import playground.app.tobeylin.weather.core.data.RecentCityRepository
import playground.app.tobeylin.weather.core.data.WeatherRepository
import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.feature.weather.state.DailyForecastItem
import playground.app.tobeylin.weather.feature.weather.state.ForecastUiState
import playground.app.tobeylin.weather.feature.weather.state.WeatherUiState
import playground.app.tobeylin.weather.feature.weather.util.calculateDewPoint
import playground.app.tobeylin.weather.feature.weather.util.formatUnixTimestampToTime
import playground.app.tobeylin.weather.feature.weather.util.metersPerSecondToKmh
import playground.app.tobeylin.weather.feature.weather.util.windDegreesToCompass
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val cityRepository: CityRepository,
    private val recentCityRepository: RecentCityRepository,
) : ViewModel() {

    private val _uiState: MutableStateFlow<WeatherUiState> = MutableStateFlow(WeatherUiState.Loading)
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _forecastUiState: MutableStateFlow<ForecastUiState> = MutableStateFlow(ForecastUiState.Loading)
    val forecastUiState: StateFlow<ForecastUiState> = _forecastUiState.asStateFlow()

    private var currentCity: City? = null
    private var loadJob: Job? = null

    fun loadCity(city: City) {
        loadJob?.cancel()
        currentCity = city
        viewModelScope.launch { recentCityRepository.saveRecentCity(city) }
        loadJob = viewModelScope.launch { loadAll() }
    }

    fun retry() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch { loadAll() }
    }

    private suspend fun loadAll() {
        _uiState.value = WeatherUiState.Loading
        _forecastUiState.value = ForecastUiState.Loading
        val city = currentCity
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
                conditionId = weather.conditionId,
                humidity = weather.humidity,
                windSpeedKmh = metersPerSecondToKmh(weather.windSpeed),
                windDirection = windDegreesToCompass(weather.windDeg),
                dewPoint = calculateDewPoint(weather.temperature, weather.humidity),
                sunriseTime = formatUnixTimestampToTime(weather.sunrise),
                sunsetTime = formatUnixTimestampToTime(weather.sunset),
            )
        } catch (e: Exception) {
            _uiState.value = WeatherUiState.Error(e.message ?: "Unknown error")
        }
    }

    private suspend fun loadForecast(city: City) {
        try {
            val forecasts = weatherRepository.getDailyForecasts(city.latitude, city.longitude)
            val items = forecasts.map { forecast ->
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.ROOT)
                val outputFormat = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
                val localizedCondition = forecast.conditionDescription.replaceFirstChar { it.uppercaseChar() }
                val date = inputFormat.parse(forecast.date) ?: return@map DailyForecastItem(
                    dayOfWeek = "",
                    conditionId = forecast.conditionId,
                    condition = localizedCondition,
                    tempMax = "${forecast.tempMax.toInt()}°",
                    tempMin = "${forecast.tempMin.toInt()}°",
                )
                DailyForecastItem(
                    dayOfWeek = outputFormat.format(date),
                    conditionId = forecast.conditionId,
                    condition = localizedCondition,
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
