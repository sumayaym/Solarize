package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "frost_data_table",
    foreignKeys = [
        ForeignKey(
            entity = RoofEntity::class,
            parentColumns = ["id"],
            childColumns = ["roofId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["roofId"], unique = true)]
)
data class FrostDataEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val roofId: Long,
    val latitude: Double,
    val longitude: Double,
    val avgTemperature: Double?,
    val avgSnowCoverage: Double?,
    val avgCloudCoverage: Double?,
    val yearlyProduction: Double?
)