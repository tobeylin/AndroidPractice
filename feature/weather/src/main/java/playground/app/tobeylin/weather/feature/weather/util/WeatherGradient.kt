package playground.app.tobeylin.weather.feature.weather.util

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Gradient color stops traced from Stitch design CSS (docs/stitch_playgroundweather/**/code.html)

// Clear: linear-gradient(135deg, #39a6ff 0%, #005f9c 50%, #feb64c 100%)
private val ClearColors = listOf(
    Color(0xFF39A6FF),
    Color(0xFF005F9C),
    Color(0xFFFEB64C),
)

// Clouds: linear-gradient(135deg, #718096 0%, #a0aec0 50%, #e2e8f0 100%)
private val CloudsColors = listOf(
    Color(0xFF718096),
    Color(0xFFA0AEC0),
    Color(0xFFE2E8F0),
)

// Rain: linear-gradient(135deg, #1e293b 0%, #0f172a 100%)
private val RainColors = listOf(
    Color(0xFF1E293B),
    Color(0xFF0F172A),
)

// Thunderstorm: linear-gradient(135deg, #1a1c2c 0%, #4a1942 50%, #1a1c2c 100%)
private val ThunderstormColors = listOf(
    Color(0xFF1A1C2C),
    Color(0xFF4A1942),
    Color(0xFF1A1C2C),
)

// Snow: linear-gradient(135deg, #f0f7ff 0%, #e2e8f0 50%, #d1e3f8 100%)
private val SnowColors = listOf(
    Color(0xFFF0F7FF),
    Color(0xFFE2E8F0),
    Color(0xFFD1E3F8),
)

// Drizzle: from-[#607d8b] via-[#90a4ae] to-[#a2bdc1]
private val DrizzleColors = listOf(
    Color(0xFF607D8B),
    Color(0xFF90A4AE),
    Color(0xFFA2BDC1),
)

// Atmosphere: linear-gradient(135deg, #d1d5db 0%, #e0e7ff 50%, #f3f4f6 100%)
private val AtmosphereColors = listOf(
    Color(0xFFD1D5DB),
    Color(0xFFE0E7FF),
    Color(0xFFF3F4F6),
)

internal fun weatherGradientColorsFor(conditionId: Int): List<Color> = when (conditionId) {
    in 200..299 -> ThunderstormColors
    in 300..399 -> DrizzleColors
    in 500..599 -> RainColors
    in 600..699 -> SnowColors
    in 700..799 -> AtmosphereColors
    800 -> ClearColors
    in 801..804 -> CloudsColors
    else -> ClearColors
}

internal fun weatherGradientBrush(conditionId: Int): Brush {
    val colors = weatherGradientColorsFor(conditionId)
    return Brush.linearGradient(colors = colors)
}

internal fun isWeatherBackgroundDark(conditionId: Int): Boolean = when (conditionId) {
    in 200..299 -> true
    in 300..399 -> true
    in 500..599 -> true
    in 600..699 -> false
    in 700..799 -> false
    800 -> true
    in 801..804 -> false
    else -> true
}

internal fun weatherCardContentColor(conditionId: Int): Color =
    if (isWeatherBackgroundDark(conditionId)) Color.White else Color(0xFF1E293B)

internal fun weatherPillColor(conditionId: Int): Color =
    if (isWeatherBackgroundDark(conditionId)) {
        Color.White.copy(alpha = 0.15f)
    } else {
        Color.Black.copy(alpha = 0.07f)
    }

// Icon tint colors traced from Stitch design CSS (docs/stitch_playgroundweather/**/code.html)
// Clear: text-yellow-300 → #FDE047
// Clouds: text-slate-100 → #F1F5F9
// Rain: text-sky-300 → #7DD3FC
// Thunderstorm: text-secondary-fixed (#CBE7F5)
// Snow: text-sky-300 → #7DD3FC
// Drizzle: text-blue-100 → #DBEAFE
// Atmosphere: text-on-surface/40% → gray
internal fun weatherIconTint(conditionId: Int): Color = when (conditionId) {
    in 200..299 -> Color(0xFFCBE7F5)
    in 300..399 -> Color(0xFFDBEAFE)
    in 500..599 -> Color(0xFF7DD3FC)
    in 600..699 -> Color(0xFF7DD3FC)
    in 700..799 -> Color(0xFF9CA3AF)
    800 -> Color(0xFFFDE047)
    in 801..804 -> Color(0xFFF1F5F9)
    else -> Color(0xFFFDE047)
}
