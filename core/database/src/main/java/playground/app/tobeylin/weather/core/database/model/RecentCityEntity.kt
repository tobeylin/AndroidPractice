package playground.app.tobeylin.weather.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_cities")
data class RecentCityEntity(
    @PrimaryKey val id: String, // "${name}_${country}" — composite key for deduplication
    val name: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val viewedAt: Long, // System.currentTimeMillis()
)
