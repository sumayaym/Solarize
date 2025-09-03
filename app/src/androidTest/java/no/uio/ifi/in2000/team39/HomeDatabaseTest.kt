package no.uio.ifi.in2000.team39

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import no.uio.ifi.in2000.team39.data.settingsForDatabase.AppDatabase
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.HomeDao
import no.uio.ifi.in2000.team39.model.databaseEntities.Home

@RunWith(AndroidJUnit4::class)
class HomeDatabaseTest {

    private lateinit var homeDao: HomeDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        homeDao = db.homeDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetHome() = runBlocking {

        // Arrange
        val expectedHome = Home(
            address = "Gaustadalleen 21, Oslo",
            latitude = 59.914,
            longitude = 10.752,
            priceArea = "Oslo"
        )
        // Act
        val id = homeDao.insertHome(expectedHome)
        val actualHome = homeDao.getHome(expectedHome.latitude, expectedHome.longitude)

        // Assert
        assertEquals(expectedHome.copy(id = id.toInt()), actualHome)
    }

    @Test
    fun getAllHomes() = runBlocking {
        // Arrange
        val home1 = Home(
            address = "Blindernveien 1, Oslo",
            latitude = 59.93,
            longitude = 10.74,
            priceArea = "Oslo"
        )
        val home2 = Home(
            address = "Problemveien 7, Oslo",
            latitude = 59.92,
            longitude = 10.76,
            priceArea = "Oslo"
        )
        // Act
        homeDao.insertHome(home1)
        homeDao.insertHome(home2)
        val allHomes = homeDao.getAllHomes()

        // Assert
        assertEquals(2, allHomes.size)
        assert(allHomes.contains(home1.copy(id = 1)))
        assert(allHomes.contains(home2.copy(id = 2)))
    }

    @Test
    fun deleteHome() = runBlocking {
        // Arrange
        val homeToDelete = Home(
            address = "Sognsveien 220, Oslo",
            latitude = 59.95,
            longitude = 10.73,
            priceArea = "Oslo"
        )

        // Act
        val id = homeDao.insertHome(homeToDelete)
        val homeBeforeDelete = homeDao.getHome(homeToDelete.latitude, homeToDelete.longitude)

        // Assert
        assertEquals(homeToDelete.copy(id = id.toInt()), homeBeforeDelete)

        // Act
        homeDao.deleteHome(homeToDelete.copy(id = id.toInt()))
        val homeAfterDelete = homeDao.getHome(homeToDelete.latitude, homeToDelete.longitude)

        // Assert
        assertNull(homeAfterDelete)
    }

    @Test
    fun deleteHomeById() = runBlocking {
        // Arrange
        val homeToDelete = Home(
            address = "Ullevålsveien 100, Oslo",
            latitude = 59.94,
            longitude = 10.72,
            priceArea = "Oslo"
        )
        // Act
        val id = homeDao.insertHome(homeToDelete)
        val homeBeforeDelete = homeDao.getHome(homeToDelete.latitude, homeToDelete.longitude)

        // Assert
        assertEquals(homeToDelete.copy(id = id.toInt()), homeBeforeDelete)

        // Act
        homeDao.deleteHomeById(id.toInt())
        val homeAfterDelete = homeDao.getHome(homeToDelete.latitude, homeToDelete.longitude)

        // Assert
        assertNull(homeAfterDelete)
    }
}