package com.example.playbox2

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VideoApi {

    @Multipart
    @POST("/api/upload-chunk")
    suspend fun uploadChunk(
        @Part chunk: MultipartBody.Part
    ): Response<ResponseBody>

    @POST("/api/merge")
    suspend fun mergeFile(
        @Body mergeRequest: MergeRequest
    ): Response<ResponseBody>
}

data class MergeRequest(
    val filename: String,
    val totalChunks: Int
)
