package playground.app.tobeylin.weather.core.data

import playground.app.tobeylin.weather.core.model.City
import playground.app.tobeylin.weather.core.network.GeocodingApi
import playground.app.tobeylin.weather.core.network.model.NetworkGeocodingResponse
import java.util.Locale
import javax.inject.Inject

internal class DefaultGeocodingRepository @Inject constructor(
    private val geocodingApi: GeocodingApi,
) : GeocodingRepository {
    override suspend fun searchCities(query: String): List<City> {
        val locale = Locale.getDefault()
        return geocodingApi.searchCities(query = query).map { response ->
            City(
                name = response.resolveLocalName(locale),
                country = Locale.Builder().setRegion(response.country).build()
                    .getDisplayCountry(locale),
                latitude = response.lat,
                longitude = response.lon,
            )
        }
    }
}

/**
 * Resolves the best localized city name from [NetworkGeocodingResponse.localNames].
 *
 * Lookup order:
 * 1. Language + country variant (e.g. "zh_tw" for zh-TW locale)
 * 2. Base language code (e.g. "zh")
 * 3. Fallback to [NetworkGeocodingResponse.name]
 */
internal fun NetworkGeocodingResponse.resolveLocalName(locale: Locale): String {
    val localNames = localNames ?: return name
    val language = locale.language
    val country = locale.country

    if (country.isNotEmpty()) {
        val variant = "${language}_${country.lowercase()}"
        localNames[variant]?.let { return it }
    }
    localNames[language]?.let { return it }
    return name
}
