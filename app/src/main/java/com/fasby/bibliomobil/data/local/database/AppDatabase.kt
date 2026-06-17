package com.fasby.bibliomobil.data.local.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.fasby.bibliomobil.data.local.dao.VolumeDao
import com.fasby.bibliomobil.data.local.entity.*

@Database(
    entities = [
        VolumeEntity::class,
        CollectionEntity::class,
        AuthorEntity::class,
        VolumeAuthorCrossRef::class,
        VolumeFtsEntity::class,
        LoanEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun volumeDao(): VolumeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "biblio_mobil_db"
                ).fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
