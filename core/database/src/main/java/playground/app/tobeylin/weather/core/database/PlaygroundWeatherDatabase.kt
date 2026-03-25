package playground.app.tobeylin.weather.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import playground.app.tobeylin.weather.core.database.dao.RecentCityDao
import playground.app.tobeylin.weather.core.database.model.RecentCityEntity

@Database(entities = [RecentCityEntity::class], version = 1, exportSchema = false)
abstract class PlaygroundWeatherDatabase : RoomDatabase() {
    abstract fun recentCityDao(): RecentCityDao
}
