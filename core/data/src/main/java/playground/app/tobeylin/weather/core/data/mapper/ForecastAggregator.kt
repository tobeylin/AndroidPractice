package playground.app.tobeylin.weather.core.data.mapper

import playground.app.tobeylin.weather.core.model.DailyForecast
import playground.app.tobeylin.weather.core.network.model.NetworkForecastItem

internal fun List<NetworkForecastItem>.toDailyForecasts(): List<DailyForecast> {
    return groupBy { it.dtTxt.substring(0, 10) }
        .entries
        .sortedBy { it.key }
        .map { (date, items) ->
            val tempMax = items.maxOf { it.main.tempMax }
            val tempMin = items.minOf { it.main.tempMin }

            val conditionSequence = items.map { it.weather.firstOrNull()?.main ?: "Unknown" }
            val conditionCounts = conditionSequence.groupingBy { it }.eachCount()
            val firstSeenIndex = mutableMapOf<String, Int>()
            conditionSequence.forEachIndexed { index, condition ->
                firstSeenIndex.putIfAbsent(condition, index)
            }

            val dominantCondition = conditionCounts.entries
                .maxWithOrNull(
                    compareBy<Map.Entry<String, Int>> { it.value }
                        .thenBy { -(firstSeenIndex[it.key] ?: Int.MAX_VALUE) },
                )
                ?.key ?: "Unknown"

            val dominantIcon = items
                .firstOrNull { (it.weather.firstOrNull()?.main ?: "Unknown") == dominantCondition }
                ?.weather
                ?.firstOrNull()
                ?.icon
                ?: ""

            DailyForecast(
                date = date,
                tempMax = tempMax,
                tempMin = tempMin,
                condition = dominantCondition,
                iconCode = dominantIcon,
            )
        }
}
