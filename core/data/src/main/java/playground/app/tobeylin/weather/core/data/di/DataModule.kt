package playground.app.tobeylin.weather.core.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import playground.app.tobeylin.weather.core.data.CityRepository
import playground.app.tobeylin.weather.core.data.DefaultCityRepository
import playground.app.tobeylin.weather.core.data.DefaultGeocodingRepository
import playground.app.tobeylin.weather.core.data.DefaultWeatherRepository
import playground.app.tobeylin.weather.core.data.GeocodingRepository
import playground.app.tobeylin.weather.core.data.WeatherRepository

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataModule {
    @Binds
    abstract fun bindsWeatherRepository(impl: DefaultWeatherRepository): WeatherRepository

    @Binds
    abstract fun bindsCityRepository(impl: DefaultCityRepository): CityRepository

    @Binds
    abstract fun bindsGeocodingRepository(impl: DefaultGeocodingRepository): GeocodingRepository
}
