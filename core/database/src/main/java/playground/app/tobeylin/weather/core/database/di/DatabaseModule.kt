package playground.app.tobeylin.weather.core.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import playground.app.tobeylin.weather.core.database.PlaygroundWeatherDatabase
import playground.app.tobeylin.weather.core.database.dao.RecentCityDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): PlaygroundWeatherDatabase =
        Room.databaseBuilder(
            context,
            PlaygroundWeatherDatabase::class.java,
            "playground_weather_db",
        ).build()

    @Provides
    fun providesRecentCityDao(database: PlaygroundWeatherDatabase): RecentCityDao =
        database.recentCityDao()
}
