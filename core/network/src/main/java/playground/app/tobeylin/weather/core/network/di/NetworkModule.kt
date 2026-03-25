package playground.app.tobeylin.weather.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import playground.app.tobeylin.weather.core.network.ApiKeyInterceptor
import playground.app.tobeylin.weather.core.network.GeocodingApi
import playground.app.tobeylin.weather.core.network.WeatherApi
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(@Named("apiKey") apiKey: String): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor(apiKey = apiKey))
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )
            .build()

    @Provides
    @Singleton
    fun providesRetrofit(
        networkJson: Json,
        okHttpClient: OkHttpClient,
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .client(okHttpClient)
        .addConverterFactory(networkJson.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun providesWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun providesGeocodingApi(retrofit: Retrofit): GeocodingApi =
        retrofit.create(GeocodingApi::class.java)
}
