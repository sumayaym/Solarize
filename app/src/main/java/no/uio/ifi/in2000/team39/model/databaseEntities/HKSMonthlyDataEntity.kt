package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "monthly_power_price_estimate",
    indices = [
        Index(value = ["area", "year", "month"], unique = true)
    ]
)
data class HKSMonthlyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val area: String,
    val year: Int,
    val month: Int,
    val averagePrice: Double
)