package de.yanneckreiss.cameraxtutorial.Api

import okhttp3.MultipartBody
import okhttp3.MultipartBody.*
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call

interface ApiService {

    data class ApiResponse(
        val success: Boolean,
        val message: String,
        val imageUrl: String? // Optional, assuming it might not be present in all responses
    )

    @Multipart
    @POST("upload/image")
    fun uploadImage(@Part image: MultipartBody.Part): Call<ApiResponse>
}