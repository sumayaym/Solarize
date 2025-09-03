package no.uio.ifi.in2000.team39.data.settingsForDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISMonthlyDataEntity

// For PVGIS data. Not everyone is used.

@Dao
interface PVGISDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPVGISData(pvgisData: PVGISDataEntity): Long

    @Query("SELECT * FROM pvgis_data_table WHERE roofId = :roofId")
    suspend fun getPVGISDataForRoof(roofId: Int): PVGISDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPVGISMonthlyData(monthlyData: List<PVGISMonthlyDataEntity>)

    @Query("DELETE FROM pvgis_data_table WHERE roofId = :roofId")
    suspend fun deletePVGISDataForRoof(roofId: Int)

    @Query("DELETE FROM pvgis_monthly_data_table WHERE roofId = :roofId")
    suspend fun deleteAllMonthlyPVGISDataForRoof(roofId: Int)

    @Query("SELECT * FROM pvgis_data_table WHERE roofId IN (SELECT id FROM roof_surface_table WHERE homeId = :homeId)")
    suspend fun getAllPVGISDataForHome(homeId: Int): List<PVGISDataEntity>

    @Query("SELECT * FROM pvgis_monthly_data_table WHERE roofId IN (SELECT id FROM roof_surface_table WHERE homeId = :homeId)")
    suspend fun getAllMonthlyPVGISDataForHome(homeId: Int): List<PVGISMonthlyDataEntity>
}