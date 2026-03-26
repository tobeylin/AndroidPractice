package playground.app.tobeylin.weather.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.Locale

internal class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (apiKey.isBlank()) throw IOException("Weather API key is not configured. Please set WEATHER_API_KEY in your gradle.properties file.")
        val originalRequest = chain.request()
        val locale = Locale.getDefault()
        val langTag = buildOpenWeatherLangTag(locale)
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("appid", apiKey)
            .addQueryParameter("lang", langTag)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }
}

/**
 * Maps the system [Locale] to an OpenWeatherMap `lang` parameter value.
 * See https://openweathermap.org/current#multi for the full list.
 */
private fun buildOpenWeatherLangTag(locale: Locale): String {
    val language = locale.language
    val country = locale.country

    return when {
        language == "zh" && country == "TW" -> "zh_tw"
        language == "zh" -> "zh_cn"
        language == "pt" && country == "BR" -> "pt_br"
        else -> language
    }
}
