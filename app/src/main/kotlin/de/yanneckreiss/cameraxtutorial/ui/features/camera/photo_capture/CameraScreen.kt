package de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import de.yanneckreiss.cameraxtutorial.core.utils.rotateBitmap
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.androidx.compose.koinViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executor


@Composable
fun CameraScreen(
    viewModel: CameraViewModel = koinViewModel()
) {
    val cameraState: CameraState by viewModel.state.collectAsStateWithLifecycle()
    val apiResponse by viewModel.apiResponse.collectAsStateWithLifecycle()
    val networkError by viewModel.networkError.collectAsState()
    if (networkError) {
        Alert(viewModel);
    }

    CameraContent(
        onPhotoCaptured = viewModel::storePhotoInGallery,

        lastCapturedPhoto = cameraState.capturedImage
    )

    apiResponse?.let {
        // Display the API response. You might want to format this or handle it differently.
        Text(it, Modifier.padding(16.dp))
    }
}


@Composable
private fun Alert(viewModel: CameraViewModel){
    AlertDialog(
        onDismissRequest = {
            // Optionally reset the networkError state when the dialog is dismissed
            viewModel.resetNetworkError()
        },
        title = { Text(text = "Network Error") },
        text = { Text(text = "Network connection problems. Please check your connection and try again.") },
        confirmButton = {
            Button(
                onClick = {
                    // Optionally reset the networkError state when the user acknowledges the error
                    viewModel.resetNetworkError()
                }
            ) {
                Text("OK")
            }
        }
    )

}

@Composable
private fun CameraContent(
    onPhotoCaptured: (Bitmap) -> Unit,
    lastCapturedPhoto: Bitmap? = null,
) {

    val context: Context = LocalContext.current
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(context) }
    Box(modifier = Modifier.fillMaxSize()) {
        // Camera preview
        AndroidView(
            //TEST
            modifier = Modifier
                .fillMaxSize(), // Assume bottom navigation bar height of 56.dp
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    scaleType = PreviewView.ScaleType.FILL_START
                }
            },
            update = { previewView ->
                previewView.controller = cameraController
                cameraController.bindToLifecycle(lifecycleOwner)
            }
        )

        // Display the last captured photo thumbnail
        lastCapturedPhoto?.let {
            LastPhotoPreview(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, bottom = 72.dp), // Adjust bottom padding to avoid navigation bar
                lastCapturedPhoto = it
            )
        }

        // FAB positioned at the top right, outside and above the bottom navigation
        ExtendedFloatingActionButton(
            text = { Text("Take photo") },
            onClick = { capturePhoto(context, cameraController, onPhotoCaptured) },
            icon = { Icon(Icons.Default.Camera, "Take Photo") },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp, end = 16.dp, bottom = 90.dp)
        )
//test

        // Simulate the Bottom Navigation bar
    }
}


fun Bitmap.toMultipartBodyPart(): MultipartBody.Part {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val requestBody = byteArrayOutputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData("image", "captured_image.jpg", requestBody)
}

private fun capturePhoto(
    context: Context,
    cameraController: LifecycleCameraController,
    onPhotoCaptured: (Bitmap) -> Unit,

) {
    val mainExecutor: Executor = ContextCompat.getMainExecutor(context)

    cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback() {
        override fun onCaptureSuccess(image: ImageProxy) {
            val correctedBitmap: Bitmap = image
                .toBitmap()
                .rotateBitmap(image.imageInfo.rotationDegrees)

            onPhotoCaptured(correctedBitmap)
            image.close()


        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraContent", "Error capturing image", exception)
        }
    })
}

@Composable
private fun LastPhotoPreview(
    modifier: Modifier = Modifier,
    lastCapturedPhoto: Bitmap
) {

    val capturedPhoto: ImageBitmap = remember(lastCapturedPhoto.hashCode()) { lastCapturedPhoto.asImageBitmap() }

    Card(
        modifier = modifier
            .size(128.dp)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Image(
            bitmap = capturedPhoto,
            contentDescription = "Last captured photo",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
    }
}

@Preview
@Composable
private fun Preview_CameraContent() {
    CameraContent(
        onPhotoCaptured = {}
    )
}
