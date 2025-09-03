package no.uio.ifi.in2000.team39.data.settingsForDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostMonthlyDataEntity

// For Frost data. Not everyone is used.

@Dao
interface FrostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrostData(frostData: FrostDataEntity): Long

    @Query("SELECT * FROM frost_data_table WHERE roofId IN (SELECT id FROM roof_surface_table WHERE homeId = :homeId)")
    suspend fun getAllFrostDataForHome(homeId: Int): List<FrostDataEntity>

    @Query("SELECT * FROM frost_data_table WHERE roofId = :roofId")
    suspend fun getFrostDataForRoof(roofId: Int): FrostDataEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFrostMonthlyData(monthlyData: List<FrostMonthlyDataEntity>)

    @Query("SELECT * FROM frost_data_table WHERE roofId IN (SELECT id FROM roof_surface_table WHERE homeId = :homeId) LIMIT 1")
    suspend fun getFirstFrostDataForHome(homeId: Int): FrostDataEntity?

    @Query("SELECT * FROM frost_monthly_data_table WHERE roofId = :roofId")
    suspend fun getAllMonthlyFrostDataForRoof(roofId: Int): List<FrostMonthlyDataEntity>

    @Query("DELETE FROM frost_data_table WHERE roofId = :roofId")
    suspend fun deleteFrostDataForRoof(roofId: Int)

    @Query("DELETE FROM frost_monthly_data_table WHERE roofId = :roofId")
    suspend fun deleteAllMonthlyFrostDataForRoof(roofId: Int)

    @Query("SELECT * FROM frost_monthly_data_table WHERE roofId IN (SELECT id FROM roof_surface_table WHERE homeId = :homeId)")
    suspend fun getAllMonthlyFrostDataForHome(homeId: Int): List<FrostMonthlyDataEntity>
}