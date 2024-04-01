package de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import de.yanneckreiss.cameraxtutorial.data.usecases.SavePhotoToGalleryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.android.annotation.KoinViewModel
import retrofit2.awaitResponse
import java.io.ByteArrayOutputStream

@KoinViewModel
class CameraViewModel(
    private val apiService: ApiService,
    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _apiResponse = MutableStateFlow<String?>(null)
    val apiResponse = _apiResponse.asStateFlow()

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private val _networkError = MutableStateFlow(false)
    val networkError = _networkError.asStateFlow()

    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
            savePhotoToGalleryUseCase.call(bitmap)
            updateCapturedPhotoState(bitmap)
            // Assume you want to upload the image right after saving
            uploadImage(bitmap)
        }
    }

    fun uploadImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _isLoading.value = true // Start loading
            try {
                val imagePart = bitmap.toMultipartBodyPart() // Make sure you've implemented this extension function
                val response = apiService.uploadImage(imagePart)
                if (response.isSuccessful && response.body() != null) {
                    _apiResponse.value = "Upload successful: ${response.body()}"
                } else {
                    _apiResponse.value = "Upload failed: ${response.errorBody()?.string()}"
                }
                // Your existing code to upload the image
            } catch (e: Exception) {
                Log.e("UploadImage", "Network connection problem: ${e.message}")
                _networkError.value = true
            } finally {
                _isLoading.value = false // Stop loading
            }
        }
    }

    
    fun Bitmap.toMultipartBodyPart(): MultipartBody.Part {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // Compress Bitmap
        val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), outputStream.toByteArray())
        return MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
    }
    private fun updateCapturedPhotoState(updatedPhoto: Bitmap?) {
        _state.value.capturedImage?.recycle()
        _state.value = _state.value.copy(capturedImage = updatedPhoto)
    }

    override fun onCleared() {
        _state.value.capturedImage?.recycle()
        super.onCleared()
    }

    fun resetNetworkError() {
        _networkError.value = false
    }
}
