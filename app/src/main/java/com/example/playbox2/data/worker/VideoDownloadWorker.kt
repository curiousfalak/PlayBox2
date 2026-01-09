package com.example.playbox2.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.playbox2.data.local.database.PlayBoxDatabase
import com.example.playbox2.data.local.entity.OfflineVideoEntity
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

class VideoDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // 🔹 Input data
            val videoId = inputData.getString("VIDEO_ID") ?: return Result.failure()
            val url = inputData.getString("VIDEO_URL") ?: return Result.failure()
            val title = inputData.getString("TITLE") ?: videoId
            val category = inputData.getString("CATEGORY") ?: "offline"

            // 🔹 Create offline directory
            val videoDir = File(applicationContext.filesDir, "offline_videos")
            if (!videoDir.exists()) videoDir.mkdirs()

            // 🔹 Safe filename (NO .mp4.mp4)
            val safeFileName = getSafeFileName(videoId)
            val outputFile = File(videoDir, safeFileName)

            // 🔹 Download ONLY if file does not exist
            if (!outputFile.exists()) {
                val request = Request.Builder().url(url).build()
                val client = OkHttpClient()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) return Result.failure()

                    response.body?.byteStream()?.use { input ->
                        outputFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                }
            }

            // 🔹 ALWAYS insert into Room DB (this creates DB)
            val db = PlayBoxDatabase.getInstance(applicationContext)
            db.videoDao().insertVideo(
                OfflineVideoEntity(
                    id = videoId,
                    title = title,
                    filePath = outputFile.absolutePath,
                    category = category
                )
            )

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    // 🔹 Prevent .mp4.mp4
    private fun getSafeFileName(fileName: String): String {
        val cleaned = fileName.trim()
        return if (cleaned.lowercase().endsWith(".mp4")) cleaned else "$cleaned.mp4"
    }
}
