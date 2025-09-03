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
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.PVGISDao
import no.uio.ifi.in2000.team39.data.settingsForDatabase.dao.RoofDao
import no.uio.ifi.in2000.team39.model.databaseEntities.Home
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.PVGISMonthlyDataEntity
import no.uio.ifi.in2000.team39.model.databaseEntities.RoofEntity

@RunWith(AndroidJUnit4::class)
class PVGISDatabaseTest {

    private lateinit var pvgisDao: PVGISDao
    private lateinit var homeDao: HomeDao
    private lateinit var roofDao: RoofDao
    private lateinit var db: AppDatabase
    private var testHomeId: Long = 0
    private var testRoofId: Long = 0

    @Before
    fun createDbAndInsertHomeAndRoof() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        pvgisDao = db.pvgisDao()
        homeDao = db.homeDao()
        roofDao = db.roofDao()

        val testHome = Home(
            address = "Fellesveien 1",
            latitude = 64.0,
            longitude = 14.0,
            priceArea = "Trøndelag"
        )
        testHomeId = homeDao.insertHome(testHome)

        val testRoof = RoofEntity(
            homeId = testHomeId.toInt(),
            roofNumber = 1,
            roofLength = 10.0,
            roofWidth = 5.0,
            roofDirection = "S",
            roofAngle = 30.0,
            panels = 10
        )
        testRoofId = roofDao.insertRoofSurface(testRoof)
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndGetPVGISDataForRoof() = runBlocking {
        // Arrange
        val expectedPVGISData = PVGISDataEntity(
            roofId = testRoofId,
            latitude = 64.0,
            longitude = 14.0,
            yearlyProduction = 1700.0
        )

        // Act
        val id = pvgisDao.insertPVGISData(expectedPVGISData)
        val actualPVGISData = pvgisDao.getPVGISDataForRoof(testRoofId.toInt())

        // Assert
        assertEquals(expectedPVGISData.copy(id = id.toInt()), actualPVGISData)
    }

    @Test
    fun insertAndGetMonthlyPVGISDataForRoof() = runBlocking {
        // Arrange
        val yearlyPVGISData = PVGISDataEntity(
            roofId = testRoofId,
            latitude = 64.0,
            longitude = 14.0,
            yearlyProduction = 1800.0
        )
        val parentId = pvgisDao.insertPVGISData(yearlyPVGISData)

        val expectedMonthlyData = listOf(
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = parentId.toInt(),
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 7,
                monthlyProduction = 160.0
            ),
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = parentId.toInt(),
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 8,
                monthlyProduction = 180.0
            )
        )

        // Act
        pvgisDao.insertAllPVGISMonthlyData(expectedMonthlyData)
        val actualMonthlyData = pvgisDao.getAllMonthlyPVGISDataForHome(testHomeId.toInt())
            .filter { it.roofId == testRoofId }

        // Assert
        assertEquals(expectedMonthlyData.size, actualMonthlyData.size)
        assert(actualMonthlyData.containsAll(expectedMonthlyData.map { it.copy(id = actualMonthlyData.find { actual -> actual.month == it.month }!!.id) }))
    }

    @Test
    fun getMonthlyPVGISDataFromRoofId() = runBlocking {
        // Arrange
        val yearlyPVGISData = PVGISDataEntity(
            roofId = testRoofId,
            latitude = 64.0,
            longitude = 14.0,
            yearlyProduction = 1900.0
        )
        val parentId = pvgisDao.insertPVGISData(yearlyPVGISData)

        val expectedMonthlyData = listOf(
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = parentId.toInt(),
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 11,
                monthlyProduction = 190.0
            ),
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = parentId.toInt(),
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 12,
                monthlyProduction = 210.0
            )
        )
        pvgisDao.insertAllPVGISMonthlyData(expectedMonthlyData)

        // Act
        val actualMonthlyData = pvgisDao.getAllMonthlyPVGISDataForHome(testHomeId.toInt())
            .filter { it.roofId == testRoofId }

        // Assert
        assertEquals(expectedMonthlyData.size, actualMonthlyData.size)
        assert(actualMonthlyData.containsAll(expectedMonthlyData.map { it.copy(id = actualMonthlyData.find { actual -> actual.month == it.month }!!.id) }))
    }

    @Test
    fun deleteRoofCascadesPVGISData() = runBlocking {
        // Arrange
        val pvgisDataToLink = PVGISDataEntity(
            roofId = testRoofId,
            latitude = 64.0,
            longitude = 14.0,
            yearlyProduction = 2000.0
        )
        pvgisDao.insertPVGISData(pvgisDataToLink)

        val monthlyDataToLink = listOf(
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = 1, // Denne ID-en vil autogenereres
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 3,
                monthlyProduction = 200.0
            ),
            PVGISMonthlyDataEntity(
                roofId = testRoofId,
                pvgisDataId = 1, // Samme referanse
                parentLatitude = 64.0,
                parentLongitude = 14.0,
                month = 4,
                monthlyProduction = 220.0
            )
        )
        pvgisDao.insertAllPVGISMonthlyData(monthlyDataToLink)

        // Act
        roofDao.deleteRoofSurface(RoofEntity(id = testRoofId.toInt(), homeId = testHomeId.toInt(), roofNumber = 1, roofLength = 10.0, roofWidth = 5.0, roofDirection = "S", roofAngle = 30.0, panels = 10))

        // Assert
        val fetchedRoof = roofDao.getRoofSurfaceById(testRoofId.toInt())
        val fetchedPVGISData = pvgisDao.getPVGISDataForRoof(testRoofId.toInt())
        val fetchedMonthlyData = pvgisDao.getAllMonthlyPVGISDataForHome(testHomeId.toInt())
            .filter { it.roofId == testRoofId }

        assertNull(fetchedRoof)
        assertNull(fetchedPVGISData)
        assertEquals(0, fetchedMonthlyData.size)
    }
}