package playground.app.tobeylin.weather.feature.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun PopularCityCard(
    cityItem: SearchCityItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        modifier = modifier.then(Modifier.testTag("city_card_${cityItem.name}")),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cityItem.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.testTag("city_name_${cityItem.name}"),
                )
                Text(
                    text = cityItem.country,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = cityItem.temperature,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("city_temp_${cityItem.name}"),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (cityItem.iconCode.isNotBlank()) {
                        Icon(
                            imageVector = weatherIconFor(cityItem.iconCode),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isWarmIcon(cityItem.iconCode)) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                    if (cityItem.condition.isNotBlank()) {
                        Text(
                            text = cityItem.condition,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PopularCityCardPreview() {
    PopularCityCard(
        cityItem = SearchCityItem(
            name = "London",
            country = "United Kingdom",
            latitude = 51.5074,
            longitude = -0.1278,
            temperature = "--",
            condition = "",
            iconCode = "",
        ),
        onClick = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PopularCityCardSunnyPreview() {
    PopularCityCard(
        cityItem = SearchCityItem(
            name = "Tokyo",
            country = "Japan",
            latitude = 35.6895,
            longitude = 139.6917,
            temperature = "22°",
            condition = "Clear",
            iconCode = "01d",
        ),
        onClick = {},
    )
}
