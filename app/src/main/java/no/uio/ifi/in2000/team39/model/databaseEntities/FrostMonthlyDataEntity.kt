package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "frost_monthly_data_table",
    foreignKeys = [
        ForeignKey(
            entity = FrostDataEntity::class,
            parentColumns = ["id"],
            childColumns = ["frostDataId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = RoofEntity::class,
            parentColumns = ["id"],
            childColumns = ["roofId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["frostDataId"]),
        Index(value = ["roofId"]),
        Index(value = ["frostDataId", "month"], unique = true)
    ]
)
data class FrostMonthlyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roofId: Long,
    val frostDataId: Int,
    val parentLatitude: Double,
    val parentLongitude: Double,
    val month: Int,
    val monthlyProduction: Double
)