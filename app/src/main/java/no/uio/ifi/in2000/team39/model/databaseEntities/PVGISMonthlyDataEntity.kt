package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pvgis_monthly_data_table",
    foreignKeys = [
        ForeignKey(
            entity = PVGISDataEntity::class,
            parentColumns = ["id"],
            childColumns = ["pvgisDataId"],
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
        Index(value = ["pvgisDataId"]),
        Index(value = ["roofId"]),
        Index(value = ["pvgisDataId", "month"], unique = true)
    ]
)
data class PVGISMonthlyDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roofId: Long,
    val pvgisDataId: Int,
    val parentLatitude: Double,
    val parentLongitude: Double,
    val month: Int,
    val monthlyProduction: Double
)