package androidx.compose.samples.crane.Api

import android.media.Image
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody
import okhttp3.MultipartBody.*
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @Serializable
    data class ApiResponse(
        val id: Int,
        val fileName: String,
        val contentType: String?,
        val detectionResult: String, // The big string containing all detection results
        val data: String? // Assuming this could be the base64 encoded image data or null
    )



    @Multipart
    @POST("images")
    suspend fun  uploadImage(@Part image: MultipartBody.Part): Response<ApiResponse>


    @GET("images")
    suspend fun fetchImages(): List<ApiResponse>



}