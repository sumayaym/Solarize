package no.uio.ifi.in2000.team39.data.settingsForDatabase.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.model.databaseEntities.Home

// For Homes.

@Dao
interface HomeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHome(home: Home): Long

    @Query("SELECT * FROM home_table")
    suspend fun getAllHomes(): List<Home>

    @Query("SELECT * FROM home_table WHERE latitude = :lat AND longitude = :lon")
    suspend fun getHome(lat: Double, lon: Double): Home?

    @Query("SELECT * FROM home_table WHERE address = :address")
    suspend fun getHomeByAddress(address: String): Home?

    @Query("SELECT * FROM home_table WHERE id = :homeId")
    suspend fun getHomeById(homeId: Int): Home?

    @Delete
    suspend fun deleteHome(home: Home)

    @Query("DELETE FROM home_table")
    suspend fun deleteAllHomes()

    @Query("DELETE FROM home_table WHERE id = :homeId")
    suspend fun deleteHomeById(homeId: Int)
}