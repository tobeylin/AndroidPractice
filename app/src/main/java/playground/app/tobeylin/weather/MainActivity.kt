package playground.app.tobeylin.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.feature.weather.SearchCitiesRoute
import playground.app.tobeylin.weather.feature.weather.WeatherHomeScreen
import playground.app.tobeylin.weather.feature.weather.WeatherViewModel
import playground.app.tobeylin.weather.ui.theme.PlaygroundWeatherTheme

private sealed interface Screen {
    data object CityList : Screen
    data class WeatherDetail(val city: City) : Screen
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlaygroundWeatherTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.CityList) }

                BackHandler(enabled = currentScreen is Screen.WeatherDetail) {
                    currentScreen = Screen.CityList
                }

                Scaffold(
                    topBar = {
                        when (val screen = currentScreen) {
                            is Screen.CityList -> {
                                // No top bar — search bar is embedded in SearchCitiesScreen
                            }
                            is Screen.WeatherDetail -> CenterAlignedTopAppBar(
                                title = { Text(screen.city.name) },
                                navigationIcon = {
                                    IconButton(onClick = { currentScreen = Screen.CityList }) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = null,
                                        )
                                    }
                                },
                            )
                        }
                    },
                ) { innerPadding ->
                    when (val screen = currentScreen) {
                        is Screen.CityList -> SearchCitiesRoute(
                            onCityClick = { city -> currentScreen = Screen.WeatherDetail(city) },
                            modifier = Modifier.padding(innerPadding),
                        )
                        is Screen.WeatherDetail -> {
                            val weatherViewModel: WeatherViewModel = hiltViewModel()
                            LaunchedEffect(screen.city) {
                                weatherViewModel.loadCity(screen.city)
                            }
                            WeatherHomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = weatherViewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}
