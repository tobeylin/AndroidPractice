package playground.app.tobeylin.weather.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import playground.app.tobeylin.weather.BuildConfig
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Named("apiKey")
    fun providesWeatherApiKey(): String = BuildConfig.WEATHER_API_KEY
}
