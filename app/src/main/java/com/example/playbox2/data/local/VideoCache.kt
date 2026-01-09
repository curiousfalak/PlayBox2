package com.example.playbox2.data.local

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@UnstableApi
object VideoCache {

    private var cache: SimpleCache? = null

    fun get(context: Context): SimpleCache {
        if (cache == null) {
            val evictor = LeastRecentlyUsedCacheEvictor(500L * 1024 * 1024)
            val dbProvider = StandaloneDatabaseProvider(context)

            cache = SimpleCache(
                File(context.cacheDir, "video_cache"),
                evictor,
                dbProvider
            )
        }
        return cache!!
    }
}

