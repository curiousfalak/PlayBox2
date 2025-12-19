package com.example.playbox2.data.local.dao

import androidx.room.*
import com.example.playbox2.data.local.entity.OfflineVideoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: OfflineVideoEntity)

    @Query("SELECT * FROM offline_videos")
    fun getAllOfflineVideos(): Flow<List<OfflineVideoEntity>>

    @Query("SELECT * FROM offline_videos WHERE category = :category")
    fun getVideosByCategory(category: String): Flow<List<OfflineVideoEntity>>

    @Query("DELETE FROM offline_videos WHERE id = :videoId")
    suspend fun deleteVideo(videoId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM offline_videos WHERE id = :videoId)")
    suspend fun isVideoDownloaded(videoId: String): Boolean
}
