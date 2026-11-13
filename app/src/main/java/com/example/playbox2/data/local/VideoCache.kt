package com.example.playbox2.data.local

import android.content.Context
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File
import android.os.SystemClock
import android.util.Log
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener

@UnstableApi
object VideoCache {
    @Volatile private var cache: SimpleCache? = null

    fun get(context: Context): SimpleCache =
        cache ?: synchronized(this) {
            cache ?: SimpleCache(
                File(context.applicationContext.filesDir, "video_cache"),
                LeastRecentlyUsedCacheEvictor(500L * 1024 * 1024),
                StandaloneDatabaseProvider(context.applicationContext)
            ).also { cache = it }
        }
}


@UnstableApi
fun attachLogs(player: ExoPlayer, cache: SimpleCache, videoUrl: String) {
    var t0 = 0L
    var seekStart = 0L

    player.addAnalyticsListener(object : AnalyticsListener {
        override fun onRenderedFirstFrame(
            eventTime: AnalyticsListener.EventTime, output: Any, renderTimeMs: Long
        ) {
            if (t0 != 0L) {
                Log.d("Perf", "startup = ${SystemClock.elapsedRealtime() - t0} ms")
                t0 = 0L
            }
        }
    })

    player.addListener(object : Player.Listener {
        override fun onPositionDiscontinuity(
            old: Player.PositionInfo, new: Player.PositionInfo, reason: Int
        ) {
            if (reason == Player.DISCONTINUITY_REASON_SEEK) seekStart = SystemClock.elapsedRealtime()
        }

        override fun onPlaybackStateChanged(state: Int) {
            if (state == Player.STATE_READY && seekStart != 0L) {
                Log.d("Perf", "seek latency = ${SystemClock.elapsedRealtime() - seekStart} ms")
                seekStart = 0L
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            Log.e("Perf", "error: ${error.message}")
        }
    })

    // call this right before player.prepare()
    t0 = SystemClock.elapsedRealtime()
    Log.d("Cache", "before play: used=${cache.cacheSpace / 1024} KB")
}