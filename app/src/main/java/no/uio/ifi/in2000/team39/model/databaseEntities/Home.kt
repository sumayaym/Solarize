package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "home_table",
    indices = [Index(value = ["address"], unique = true)]
)
data class Home(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val priceArea: String
)

