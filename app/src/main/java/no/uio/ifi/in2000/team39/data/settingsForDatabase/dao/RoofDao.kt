package no.uio.ifi.in2000.team39.data.settingsForDatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity

// For mutiple roofs. A home can have many roofs.

@Dao
interface RoofDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoofSurface(roofSurface: RoofEntity): Long

    @Query("SELECT * FROM roof_surface_table WHERE homeId = :homeId ORDER BY roofNumber ASC")
    suspend fun getAllRoofSurfacesForHome(homeId: Int): List<RoofEntity>

    @Query("SELECT * FROM roof_surface_table WHERE id = :roofSurfaceId")
    suspend fun getRoofSurfaceById(roofSurfaceId: Int): RoofEntity?

    @Delete
    suspend fun deleteRoofSurface(roofSurface: RoofEntity)

    @Query("DELETE FROM roof_surface_table WHERE homeId = :homeId")
    suspend fun deleteAllRoofSurfacesForHome(homeId: Int)
}