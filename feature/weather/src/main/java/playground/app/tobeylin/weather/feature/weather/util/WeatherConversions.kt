package playground.app.tobeylin.weather.feature.weather.util

import kotlin.math.ln

internal fun metersPerSecondToKmh(mps: Double): Double = mps * 3.6

internal fun windDegreesToCompass(degrees: Int?): String {
    degrees ?: return ""
    val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
    val index = (((degrees.toDouble() % 360 + 360) % 360 + 11.25) / 22.5).toInt() % 16
    return directions[index]
}

internal fun calculateDewPoint(tempCelsius: Double, humidityPercent: Int): Double {
    val rh = humidityPercent.coerceIn(1, 100)
    val a = 17.27
    val b = 237.7
    val alpha = (a * tempCelsius) / (b + tempCelsius) + ln(rh / 100.0)
    return (b * alpha) / (a - alpha)
}
