package de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture


import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.samples.crane.Api.ApiService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.android.MediaManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.awaitResponse
import java.io.ByteArrayOutputStream
import androidx.compose.runtime.State
import okhttp3.HttpUrl.Companion.toHttpUrl

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val apiService: ApiService,
//    private val savePhotoToGalleryUseCase: SavePhotoToGalleryUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _apiResponse = MutableStateFlow<String?>(null)
    val apiResponse = _apiResponse.asStateFlow()

    private val _state = MutableStateFlow(CameraState())
    val state = _state.asStateFlow()

    private val _imageBitmap = mutableStateOf<Bitmap?>(null)
    val imageBitmap: State<Bitmap?> = _imageBitmap

    private val _networkError = MutableStateFlow(false)
    val networkError = _networkError.asStateFlow()




    fun storePhotoInGallery(bitmap: Bitmap) {
        viewModelScope.launch {
//            savePhotoToGalleryUseCase.call(bitmap)
            updateCapturedPhotoState(bitmap)
            uploadImage(bitmap)
        }
    }

    fun uploadImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _apiResponse.value = null;
            _isLoading.value = true
            try {
//                uploadImageToCloudinary(bitmap)
                val imagePart = bitmap.toMultipartBodyPart()
                val response = apiService.uploadImage(imagePart)
                if (response.isSuccessful && response.body() != null) {

                    _apiResponse.value = response.body()!!.detectionResult
                } else {
                    _apiResponse.value = "Upload failed: ${response.errorBody()?.string()}"
                }
            } catch (e: Exception) {
                Log.e("UploadImage", "Network connection problem: ${e.message}")
                _networkError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

//    fun uploadImageToCloudinary(bitmap: Bitmap) {
//            val stream = ByteArrayOutputStream()
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
//            val byteArray = stream.toByteArray()
//
//            viewModelScope.launch {
//                try {
//                    val uploadResult = MediaManager.get().upload(byteArray)
//                        .option("resource_type", "image")
//                        .option("folder", "sample_folder/") // Optional: specify a folder
//                        .dispatch()
//                    val url = uploadResult.toHttpUrl();
//                    println(url)
//                    // Handle upload success on the UI thread
//                    withContext(Dispatchers.Main) {
//                        Log.d("UploadSuccess", "Uploaded: " + uploadResult.toHttpUrl())
//                        // Update UI or notify user of success
//                    }
//                } catch (e: Exception) {
//                    // Handle upload error
//                    Log.e("UploadError", "Error uploading image", e)
//                }
//            }
//
//    }
    fun updateImageBitmap(bitmap: Bitmap?) {
        _imageBitmap.value = bitmap
    }
    fun Bitmap.toMultipartBodyPart(): MultipartBody.Part {
        val outputStream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 72, outputStream)
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

@Serializable
data class DetectionResponse(
    val id: Int,
    val fileName: String?,
    val contentType: String?,
    val detectionResult: String,
    val data: String?
)