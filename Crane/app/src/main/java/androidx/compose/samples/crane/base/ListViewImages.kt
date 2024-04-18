/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.samples.crane.base

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.samples.crane.Api.ApiService
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.MainViewModel
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.CameraViewModel

@Composable
fun ListViewImages(
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    title: String,
    onItemClicked: OnExploreItemClicked,
    viewModel: MainViewModel
) {
    val imageList by viewModel.images

//    val cameraState: CameraState by cameraViewModel.state.collectAsStateWithLifecycle()

//    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp), // Adjust based on your UI design preference
            modifier = modifier.padding(8.dp), // Add padding around the grid
            verticalArrangement = Arrangement.spacedBy(8.dp), // Add vertical spacing between items
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

                items(imageList.size) { index ->
                    ImageCard(imageData = imageList[index])
                }


        }
    }
}







@Composable
fun ImageCard(imageData: ApiService.ApiResponse) {
    val imageBitmap = remember(imageData.data) { imageData.data?.base64ToImageBitmap() }

    Card(
        modifier = Modifier
            .padding(4.dp) // Adjust padding to control spacing between grid items
            .aspectRatio(1f) // Ensures that the card is always square
            .fillMaxWidth(), // Ensures the Card expands to fill the column width
        elevation = 4.dp
    ) {
        imageBitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Loaded image",
                modifier = Modifier.fillMaxSize(), // Fill the card
                contentScale = ContentScale.Crop // Crop to cover the Card area
            )
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

/**
 * Composable with large image card and text underneath.
 */
@Composable
private fun ExploreItemColumn(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Column(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer(modifier = Modifier.fillMaxWidth()) {
            ExploreImage(item)
        }
        Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.subtitle1
            )
            Spacer(Modifier.height(4.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreItemRow(
    modifier: Modifier = Modifier,
    item: ExploreModel,
    onItemClicked: OnExploreItemClicked
) {
    Row(
        modifier = modifier
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp)
    ) {
        ExploreImageContainer(modifier = Modifier.size(64.dp)) {
            ExploreImage(item)
        }
        Spacer(Modifier.width(24.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = item.city.nameToDisplay,
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.description,
                style = MaterialTheme.typography.caption.copy(color = crane_caption)
            )
        }
    }
}

@Composable
private fun ExploreImage(item: ExploreModel) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
private fun ExploreImageContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier
            .wrapContentHeight()
            .fillMaxWidth(), RoundedCornerShape(4.dp)) {
        content()
    }
}



//@Preview
//@Composable
//fun ExploreSectiontest(
//    widthSize: WindowWidthSizeClass,
//    modifier: Modifier = Modifier,
//) {
//
//    var onItemClicked = null;
//    modifier = null;
//    var exploreList = null;
//    var title = "test"
//    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
//        Column(modifier = Modifier.padding(start = 24.dp, top = 20.dp, end = 24.dp)) {
//            Text(
//                text = title,
//                style = MaterialTheme.typography.caption.copy(color = crane_caption)
//            )
//            Spacer(Modifier.height(8.dp))
//
//            LazyVerticalStaggeredGrid(
//                columns = StaggeredGridCells.Adaptive(200.dp),
//                modifier = Modifier.fillMaxWidth().imePadding(),
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                content = {
//                    itemsIndexed(exploreList) { _, exploreItem ->
//                        when (widthSize) {
//                            WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
//                                ExploreItemColumn(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    item = exploreItem,
//                                    onItemClicked = onItemClicked
//                                )
//                            }
//                            else -> {
//                                ExploreItemRow(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    item = exploreItem,
//                                    onItemClicked = onItemClicked
//                                )
//                            }
//                        }
//                    }
//                    item(span = StaggeredGridItemSpan.FullLine) {
//                        Spacer(
//                            modifier = Modifier
//                                .windowInsetsBottomHeight(WindowInsets.navigationBars)
//                        )
//                    }
//                }
//            )
//        }
//    }
//}
