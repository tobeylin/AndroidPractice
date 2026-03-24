package playground.app.tobeylin.weather.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun WeatherHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val forecastState by viewModel.forecastUiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        when (val state = uiState) {
            is WeatherUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is WeatherUiState.Success -> {
                TodayWeatherCard(
                    uiState = state,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WeatherMetricCard(
                        icon = Icons.Filled.WaterDrop,
                        label = "Humidity",
                        value = "${state.humidity}%",
                        subtitle = "Dew point is ${state.dewPoint.toInt()}°",
                        modifier = Modifier.weight(1f),
                    )
                    WeatherMetricCard(
                        icon = Icons.Filled.Air,
                        label = "Wind",
                        value = "${state.windSpeedKmh.toInt()} km/h",
                        subtitle = if (state.windDirection.isNotEmpty()) "${state.windDirection} Direction" else "",
                        modifier = Modifier.weight(1f),
                    )
                }

                val forecast = forecastState
                if (forecast is ForecastUiState.Success && forecast.items.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    FiveDayForecastCard(
                        items = forecast.items,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            is WeatherUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeatherHomeScreenPreview() {
    val state = WeatherUiState.Success(
        cityName = "Taipei",
        temperature = 24.0,
        tempMax = 28.0,
        tempMin = 18.0,
        condition = "Clear",
        conditionDescription = "clear sky",
        iconCode = "01d",
        humidity = 64,
        windSpeedKmh = 12.0,
        windDirection = "NW",
        dewPoint = 14.0,
    )
    Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {
        TodayWeatherCard(uiState = state, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            WeatherMetricCard(
                icon = Icons.Filled.WaterDrop,
                label = "Humidity",
                value = "${state.humidity}%",
                subtitle = "Dew point is ${state.dewPoint.toInt()}°",
                modifier = Modifier.weight(1f),
            )
            WeatherMetricCard(
                icon = Icons.Filled.Air,
                label = "Wind",
                value = "${state.windSpeedKmh.toInt()} km/h",
                subtitle = if (state.windDirection.isNotEmpty()) "${state.windDirection} Direction" else "",
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        FiveDayForecastCard(
            items = listOf(
                DailyForecastItem("Tue", "01d", "Sunny", "26°", "17°"),
                DailyForecastItem("Wed", "02d", "Partly Cloudy", "24°", "16°"),
                DailyForecastItem("Thu", "04d", "Cloudy", "22°", "15°"),
                DailyForecastItem("Fri", "10d", "Light Rain", "19°", "14°"),
                DailyForecastItem("Sat", "04d", "Cloudy", "21°", "15°"),
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
