package no.uio.ifi.in2000.team39.data.settingsForDatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.model.databaseEntities.HKSMonthlyDataEntity

@Dao
interface HKSDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMonthlyData(monthlyData: List<HKSMonthlyDataEntity>)

    @Query("SELECT * FROM monthly_power_price_estimate WHERE area = :area AND year = :year ORDER BY month ASC")
    suspend fun getAllMonthlyDataForYear(area: String, year: Int): List<HKSMonthlyDataEntity>

    @Query("DELETE FROM monthly_power_price_estimate WHERE area = :area AND year = :year")
    suspend fun deleteAllMonthlyDataForYear(area: String, year: Int)

    @Query("DELETE FROM monthly_power_price_estimate")
    suspend fun deleteAllMonthlyData()


}