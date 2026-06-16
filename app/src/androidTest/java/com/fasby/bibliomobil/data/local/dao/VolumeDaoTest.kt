package com.fasby.bibliomobil.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fasby.bibliomobil.data.local.database.AppDatabase
import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.VolumeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VolumeDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var volumeDao: VolumeDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        volumeDao = database.volumeDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertAndGetVolume() = runBlocking {
        val volume = VolumeEntity(
            isbn = "1234567890",
            title = "Test Book",
            collectionId = null,
            number = 1,
            publishedYear = 2024,
            synopsis = "A great test book",
            coverPath = "",
            rating = 5
        )
        val authors = listOf(AuthorEntity("1", "John Doe", "Author"))
        
        volumeDao.insertCompleteVolume(volume, authors)
        
        val detailedVolume = volumeDao.getVolumeByIsbn("1234567890")
        
        assertEquals("Test Book", detailedVolume?.volume?.title)
        assertEquals(1, detailedVolume?.authors?.size)
        assertEquals("John Doe", detailedVolume?.authors?.first()?.name)
    }

    @Test
    fun searchVolumesFts() = runBlocking {
        val v1 = VolumeEntity("1", null, "Batman: Year One", 0, 1987, "Early years", "", 5)
        val v2 = VolumeEntity("2", null, "Superman: Red Son", 0, 2003, "Alternative reality", "", 4)
        
        volumeDao.insertVolume(v1)
        volumeDao.insertVolume(v2)
        
        // Search by title
        val results = volumeDao.searchVolumesFts("Batman").first()
        assertEquals(1, results.size)
        assertEquals("Batman: Year One", results[0].volume.title)
        
        // Search by synopsis
        val results2 = volumeDao.searchVolumesFts("reality").first()
        assertEquals(1, results2.size)
        assertEquals("Superman: Red Son", results2[0].volume.title)
    }

    @Test
    fun deleteVolumeWorks() = runBlocking {
        val volume = VolumeEntity("1", null, "To Delete", 0, 2000, "", "", 0)
        volumeDao.insertVolume(volume)
        
        val before = volumeDao.getAllDetailedVolumes().first()
        assertEquals(1, before.size)
        
        volumeDao.deleteVolume(volume)
        
        val after = volumeDao.getAllDetailedVolumes().first()
        assertTrue(after.isEmpty())
    }
}
