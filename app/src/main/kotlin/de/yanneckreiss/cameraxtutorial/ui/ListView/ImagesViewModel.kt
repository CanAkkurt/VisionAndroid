package de.yanneckreiss.cameraxtutorial.ui.ListView

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ImagesViewModel(private val apiService: ApiService) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _images = MutableStateFlow<List<ApiService.ApiResponse>>(emptyList())
    val images: StateFlow<List<ApiService.ApiResponse>> = _images
    init {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _images.value = apiService.fetchImages()
            }catch (e:Exception){
                // Stop loading

            }finally {
                _isLoading.value = false
            }

        }
    }
}