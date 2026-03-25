package playground.app.tobeylin.weather.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val recentCitiesUiState by viewModel.recentCitiesUiState.collectAsStateWithLifecycle()
    val searchResultsUiState by viewModel.searchResultsUiState.collectAsStateWithLifecycle()

    SearchCitiesScreen(
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onClearSearch = viewModel::onClearSearch,
        recentCitiesUiState = recentCitiesUiState,
        searchResultsUiState = searchResultsUiState,
        onCityClick = onCityClick,
        modifier = modifier,
    )
}

@Composable
fun SearchCitiesScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    recentCitiesUiState: RecentCitiesUiState,
    searchResultsUiState: SearchResultsUiState,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        CitySearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            onClearClick = onClearSearch,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
        )

        if (searchQuery.isNotBlank()) {
            when (searchResultsUiState) {
                SearchResultsUiState.Idle -> {
                    // Do nothing
                }
                SearchResultsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is SearchResultsUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("search_results_list"),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            Text(
                                text = "SEARCH RESULTS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            )
                        }
                        items(searchResultsUiState.cities) { item ->
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
                is SearchResultsUiState.Empty -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("no_results"),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "No results found for \"${searchResultsUiState.query}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is SearchResultsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = searchResultsUiState.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        } else {
            when (recentCitiesUiState) {
                RecentCitiesUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }
                RecentCitiesUiState.Empty -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("empty_state"),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(bottom = 16.dp),
                        )
                        Text(
                            text = "No recently viewed locations",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Text(
                            text = "Search for a city to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                is RecentCitiesUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("recent_cities_list"),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            Text(
                                text = "RECENTLY VIEWED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                            )
                        }
                        items(recentCitiesUiState.cities) { item ->
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
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenRecentPreview() {
    SearchCitiesScreen(
        searchQuery = "",
        onSearchQueryChange = {},
        onClearSearch = {},
        recentCitiesUiState = RecentCitiesUiState.Success(
            cities = listOf(
                SearchCityItem("London", "United Kingdom", 51.5, -0.1),
                SearchCityItem("Tokyo", "Japan", 35.7, 139.7),
                SearchCityItem("New York", "United States", 40.7, -74.0),
            )
        ),
        searchResultsUiState = SearchResultsUiState.Idle,
        onCityClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenEmptyPreview() {
    SearchCitiesScreen(
        searchQuery = "",
        onSearchQueryChange = {},
        onClearSearch = {},
        recentCitiesUiState = RecentCitiesUiState.Empty,
        searchResultsUiState = SearchResultsUiState.Idle,
        onCityClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenSearchResultsPreview() {
    SearchCitiesScreen(
        searchQuery = "London",
        onSearchQueryChange = {},
        onClearSearch = {},
        recentCitiesUiState = RecentCitiesUiState.Success(emptyList()),
        searchResultsUiState = SearchResultsUiState.Success(
            cities = listOf(
                SearchCityItem("London", "United Kingdom", 51.5, -0.1),
                SearchCityItem("London", "Canada", 42.9, -81.2),
                SearchCityItem("East London", "South Africa", -33.0, 27.9),
            )
        ),
        onCityClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun SearchCitiesScreenNoResultsPreview() {
    SearchCitiesScreen(
        searchQuery = "Zzz",
        onSearchQueryChange = {},
        onClearSearch = {},
        recentCitiesUiState = RecentCitiesUiState.Success(emptyList()),
        searchResultsUiState = SearchResultsUiState.Empty("Zzz"),
        onCityClick = {},
    )
}
