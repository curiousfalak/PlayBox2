package com.example.playbox2.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playbox2.data.local.dao.VideoDao
import com.example.playbox2.data.local.entity.OfflineVideoEntity

@Database(
    entities = [OfflineVideoEntity::class],
    version = 1
)
abstract class PlayBoxDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}
