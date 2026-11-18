package tn.isetr.safewalk.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trajet_points")
data class TrajetPoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)