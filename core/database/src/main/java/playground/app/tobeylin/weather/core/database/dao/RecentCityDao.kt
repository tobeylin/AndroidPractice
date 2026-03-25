package playground.app.tobeylin.weather.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import playground.app.tobeylin.weather.core.database.model.RecentCityEntity

@Dao
interface RecentCityDao {

    @Query("SELECT * FROM recent_cities ORDER BY viewedAt DESC LIMIT :limit")
    fun getRecentCities(limit: Int = 3): Flow<List<RecentCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCity(city: RecentCityEntity)

    @Query(
        "DELETE FROM recent_cities WHERE id NOT IN " +
            "(SELECT id FROM recent_cities ORDER BY viewedAt DESC LIMIT :keepCount)",
    )
    suspend fun pruneOldEntries(keepCount: Int = 3)
}
