package playground.app.tobeylin.weather.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

internal class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (apiKey.isBlank()) throw IOException("Weather API key is not configured. Please set WEATHER_API_KEY in your gradle.properties file.")
        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("appid", apiKey)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }
}
