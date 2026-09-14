package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SavedPostEntity::class], version = 1, exportSchema = false)
abstract class TurkgramDatabase : RoomDatabase() {
    abstract fun savedPostDao(): SavedPostDao

    companion object {
        @Volatile
        private var INSTANCE: TurkgramDatabase? = null

        fun getInstance(context: Context): TurkgramDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TurkgramDatabase::class.java,
                    "turkgram_database"
                ).fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
