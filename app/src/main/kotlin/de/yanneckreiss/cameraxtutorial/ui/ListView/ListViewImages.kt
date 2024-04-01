package de.yanneckreiss.cameraxtutorial.ui.ListView

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.LottieLoadingAnimation
import org.koin.androidx.compose.koinViewModel

@Preview
@Composable
fun ImagesScreen(viewModel: ImagesViewModel = koinViewModel()) {
    val images by viewModel.images.collectAsState()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    LazyColumn {
        items(images.size) { index ->
            ImageCard(imageData = images[index])
        }
    }
    LottieLoadingAnimation(isLoading = isLoading)
}

@Composable
fun ImageCard(imageData: ApiService.ApiResponse) {
    val imageBitmap = remember(imageData.data) { imageData.data?.base64ToImageBitmap() }
    Card(modifier = Modifier.padding(8.dp)) {
        imageBitmap?.let {
            Image(bitmap = it, contentDescription = "Loaded image", modifier = Modifier.padding(8.dp))
        } ?: Text("Image unavailable", modifier = Modifier.padding(8.dp))
    }
}


fun String.base64ToImageBitmap(): ImageBitmap? {
    return try {
        val imageBytes = Base64.decode(this, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap.asImageBitmap()
    } catch (e: IllegalArgumentException) {
        // Handle error
        null
    }
}


