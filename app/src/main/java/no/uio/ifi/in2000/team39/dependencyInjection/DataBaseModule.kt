package no.uio.ifi.in2000.team39.dependencyInjection

import android.content.Context
import androidx.room.Room.databaseBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.uio.ifi.in2000.team39.data.settingsForDatabase.AppDatabase
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.FrostDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HKSDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HomeDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.PVGISDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.RoofDao
import javax.inject.Singleton


/**
  Dependency injection for the database.
**/

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return databaseBuilder(
            context,
            AppDatabase::class.java,
            "wattWise_database"

        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideHomeDao(appDatabase: AppDatabase): HomeDao {
        return appDatabase.homeDao()
    }

    @Provides
    fun provideFrostDataDao(appDatabase: AppDatabase): FrostDao {
        return appDatabase.frostDao()
    }

    @Provides
    fun providePVGISDao(appDatabase: AppDatabase): PVGISDao {
        return appDatabase.pvgisDao()
    }

    @Provides
    fun provideHKSDao(appDatabase: AppDatabase): HKSDao {
        return appDatabase.hksDao()
    }

    @Provides
    fun provideRoofDao(appDatabase: AppDatabase): RoofDao {
        return appDatabase.roofDao()
    }
}