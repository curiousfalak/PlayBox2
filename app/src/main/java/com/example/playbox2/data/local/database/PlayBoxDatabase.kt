package com.example.playbox2.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.work.impl.Migration_1_2
import com.example.playbox2.data.local.dao.VideoDao
import com.example.playbox2.data.local.entity.OfflineVideoEntity


@Database(
    entities = [OfflineVideoEntity::class],
    version = 3,
    exportSchema = false
)
abstract class PlayBoxDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao

    companion object {
        @Volatile private var INSTANCE: PlayBoxDatabase? = null

        fun getInstance(context: Context): PlayBoxDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PlayBoxDatabase::class.java,
                    "playbox_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }

            }
            }
    }
