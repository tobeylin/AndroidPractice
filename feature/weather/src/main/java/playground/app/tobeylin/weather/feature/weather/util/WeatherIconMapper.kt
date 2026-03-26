package playground.app.tobeylin.weather.feature.weather.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Maps an OpenWeatherMap condition ID to a Material Icon.
 *
 * Condition ID ranges follow the official specification:
 * - 2xx  Thunderstorm
 * - 3xx  Drizzle
 * - 5xx  Rain  (511 freezing rain → AcUnit)
 * - 6xx  Snow
 * - 7xx  Atmosphere (mist, fog, haze, …)
 * - 800  Clear
 * - 801  Few clouds
 * - 802  Scattered clouds
 * - 803–804  Broken / overcast clouds
 *
 * @see <a href="https://openweathermap.org/weather-conditions">OWM Weather Condition Codes</a>
 */
internal fun weatherIconFor(conditionId: Int): ImageVector = when (conditionId) {
    in 200..299 -> Icons.Filled.Thunderstorm
    in 300..399 -> Icons.Filled.WaterDrop
    511 -> Icons.Filled.AcUnit
    in 500..599 -> Icons.Filled.WaterDrop
    in 600..699 -> Icons.Filled.AcUnit
    in 700..799 -> Icons.Filled.Dehaze
    800 -> Icons.Filled.WbSunny
    801 -> Icons.Filled.WbCloudy
    in 802..804 -> Icons.Filled.Cloud
    else -> Icons.Filled.Cloud
}
