package playground.app.tobeylin.weather.feature.weather.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import playground.app.tobeylin.weather.feature.weather.state.WeatherUiState
import playground.app.tobeylin.weather.feature.weather.util.isWeatherBackgroundDark
import playground.app.tobeylin.weather.feature.weather.util.weatherCardContentColor
import playground.app.tobeylin.weather.feature.weather.util.weatherIconTint
import playground.app.tobeylin.weather.feature.weather.util.weatherGradientBrush
import playground.app.tobeylin.weather.feature.weather.util.weatherIconFor
import playground.app.tobeylin.weather.feature.weather.util.weatherPillColor

@Composable
fun TodayWeatherCard(
    uiState: WeatherUiState.Success,
    modifier: Modifier = Modifier,
) {
    val brush = weatherGradientBrush(uiState.conditionId)
    val contentColor = weatherCardContentColor(uiState.conditionId)
    val pillColor = weatherPillColor(uiState.conditionId)
    val overlayAlpha = if (isWeatherBackgroundDark(uiState.conditionId)) 0.12f else 0.08f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(brush),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val topRightCenter = Offset(size.width * 0.85f, size.height * 0.15f)
            val topRightRadius = size.minDimension * 0.45f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = overlayAlpha), Color.Transparent),
                    center = topRightCenter,
                    radius = topRightRadius,
                ),
                radius = topRightRadius,
                center = topRightCenter,
            )

            val bottomLeftCenter = Offset(size.width * 0.15f, size.height * 0.85f)
            val bottomLeftRadius = size.minDimension * 0.30f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = overlayAlpha), Color.Transparent),
                    center = bottomLeftCenter,
                    radius = bottomLeftRadius,
                ),
                radius = bottomLeftRadius,
                center = bottomLeftCenter,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${uiState.temperature.toInt()}°",
                    style = MaterialTheme.typography.displayLarge,
                    color = contentColor,
                )
                Icon(
                    imageVector = weatherIconFor(uiState.conditionId),
                    contentDescription = uiState.condition,
                    modifier = Modifier.size(64.dp),
                    tint = weatherIconTint(uiState.conditionId),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = uiState.conditionDescription.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                color = contentColor,
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TemperaturePill(label = "↑", temperature = uiState.tempMax, contentColor = contentColor, pillColor = pillColor)
                TemperaturePill(label = "↓", temperature = uiState.tempMin, contentColor = contentColor, pillColor = pillColor)
            }
        }
    }
}

@Composable
private fun TemperaturePill(
    label: String,
    temperature: Double,
    contentColor: Color,
    pillColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(50),
        color = pillColor,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
            )
            Text(
                text = "${temperature.toInt()}°C",
                style = MaterialTheme.typography.titleSmall,
                color = contentColor,
            )
        }
    }
}

private class WeatherConditionProvider : PreviewParameterProvider<WeatherUiState.Success> {
    override val values = sequenceOf(
        previewState(conditionId = 800, condition = "Clear", description = "clear sky"),
        previewState(conditionId = 802, condition = "Clouds", description = "scattered clouds"),
        previewState(conditionId = 501, condition = "Rain", description = "moderate rain"),
        previewState(conditionId = 211, condition = "Thunderstorm", description = "thunderstorm"),
        previewState(conditionId = 601, condition = "Snow", description = "snow"),
        previewState(conditionId = 301, condition = "Drizzle", description = "drizzle"),
        previewState(conditionId = 741, condition = "Atmosphere", description = "fog"),
    )
}

private fun previewState(
    conditionId: Int,
    condition: String,
    description: String,
) = WeatherUiState.Success(
    cityName = "Taipei",
    temperature = 24.0,
    tempMax = 28.0,
    tempMin = 18.0,
    condition = condition,
    conditionDescription = description,
    conditionId = conditionId,
    humidity = 64,
    windSpeedKmh = 12.0,
    windDirection = "NW",
    dewPoint = 14.0,
)

@Preview(showBackground = true)
@Composable
private fun TodayWeatherCardPreview(
    @PreviewParameter(WeatherConditionProvider::class) uiState: WeatherUiState.Success,
) {
    TodayWeatherCard(uiState = uiState)
}

@Preview(showBackground = true)
@Composable
private fun TodayWeatherCardLoadingPreview() {
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.primaryContainer,
    )
    val brush = Brush.linearGradient(colors = colors)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(MaterialTheme.shapes.large)
            .background(brush),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
    }
}
