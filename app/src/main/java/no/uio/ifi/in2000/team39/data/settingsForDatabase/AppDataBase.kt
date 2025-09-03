package no.uio.ifi.in2000.team39.data.settingsForDatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.FrostDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HKSDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HomeDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.PVGISDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.RoofDao
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.FrostMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.HKSMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity


/**
 Needed to set up the database.
 */

@Database(

    entities = [Home::class, FrostDataEntity::class, FrostMonthlyDataEntity::class, PVGISDataEntity::class, PVGISMonthlyDataEntity::class, HKSMonthlyDataEntity::class, RoofEntity::class],

    version = 4,

    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun homeDao(): HomeDao
    abstract fun frostDao(): FrostDao
    abstract fun pvgisDao(): PVGISDao
    abstract fun hksDao(): HKSDao
    abstract fun roofDao(): RoofDao

}
