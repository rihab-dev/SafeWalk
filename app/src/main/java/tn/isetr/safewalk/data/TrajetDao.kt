package tn.isetr.safewalk.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TrajetDao {
    @Insert
    suspend fun insert(point: TrajetPoint)

    @Query("SELECT * FROM trajet_points ORDER BY timestamp DESC")
    fun getAll(): Flow<List<TrajetPoint>>

    @Query("DELETE FROM trajet_points")
    suspend fun clear()
}