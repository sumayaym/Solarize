package no.uio.ifi.in2000.team39.model.databaseEntities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "roof_surface_table",
    foreignKeys = [
        ForeignKey(
            entity = Home::class,
            parentColumns = ["id"],
            childColumns = ["homeId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoofEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val homeId: Int,
    val roofNumber: Int,
    val roofLength: Double,
    val roofWidth: Double,
    val roofDirection: String,
    val roofAngle: Double,
    val panels: Int
)