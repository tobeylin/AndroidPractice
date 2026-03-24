package playground.app.tobeylin.weather.core.network

import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.newBuilder()
            .addQueryParameter("appid", apiKey)
            .build()
        return chain.proceed(originalRequest.newBuilder().url(url).build())
    }
}
