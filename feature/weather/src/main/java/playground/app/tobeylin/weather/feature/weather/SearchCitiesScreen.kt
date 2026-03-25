package playground.app.tobeylin.weather.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import playground.app.tobeylin.weather.core.model.City

@Composable
fun SearchCitiesRoute(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchCitiesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.recentCitiesUiState.collectAsStateWithLifecycle()
    SearchCitiesScreen(
        uiState = uiState,
        onCityClick = onCityClick,
        modifier = modifier,
    )
}

@Composable
fun SearchCitiesScreen(
    uiState: RecentCitiesUiState,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is RecentCitiesUiState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is RecentCitiesUiState.Empty -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "No recent cities",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
        is RecentCitiesUiState.Success -> {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .testTag("popular_cities_list"),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Text(
                        text = "POPULAR CITIES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
                items(uiState.cities) { item ->
                    PopularCityCard(
                        cityItem = item,
                        onClick = {
                            onCityClick(
                                City(
                                    name = item.name,
                                    country = item.country,
                                    latitude = item.latitude,
                                    longitude = item.longitude,
                                )
                            )
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenSuccessPreview() {
    SearchCitiesScreen(
        uiState = RecentCitiesUiState.Success(
            cities = listOf(
                SearchCityItem("London", "United Kingdom", 51.5, -0.1),
                SearchCityItem("Tokyo", "Japan", 35.7, 139.7),
                SearchCityItem("New York", "United States", 40.7, -74.0),
                SearchCityItem("Paris", "France", 48.9, 2.3),
                SearchCityItem("Sydney", "Australia", -33.9, 151.2),
            )
        ),
        onCityClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenLoadingPreview() {
    SearchCitiesScreen(
        uiState = RecentCitiesUiState.Loading,
        onCityClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenEmptyPreview() {
    SearchCitiesScreen(
        uiState = RecentCitiesUiState.Empty,
        onCityClick = {},
    )
}
