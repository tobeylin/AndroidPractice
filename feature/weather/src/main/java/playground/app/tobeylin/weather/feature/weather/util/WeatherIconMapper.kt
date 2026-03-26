package playground.app.tobeylin.weather.feature.weather.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Dehaze
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

internal fun weatherIconFor(iconCode: String): ImageVector = when (iconCode) {
    "01d", "01n" -> Icons.Filled.WbSunny
    "02d", "02n" -> Icons.Filled.WbCloudy
    "03d", "03n", "04d", "04n" -> Icons.Filled.Cloud
    "09d", "09n", "10d", "10n" -> Icons.Filled.WaterDrop
    "11d", "11n" -> Icons.Filled.Thunderstorm
    "13d", "13n" -> Icons.Filled.AcUnit
    "50d", "50n" -> Icons.Filled.Dehaze
    else -> Icons.Filled.Cloud
}

internal fun isWarmIcon(iconCode: String): Boolean = iconCode in setOf("01d", "01n", "02d", "02n")
