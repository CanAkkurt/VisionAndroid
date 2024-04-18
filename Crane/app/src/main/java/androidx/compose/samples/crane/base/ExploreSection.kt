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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.samples.crane.data.ExploreModel
import androidx.compose.samples.crane.home.OnExploreItemClicked
import androidx.compose.samples.crane.ui.crane_caption
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.CameraState
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.CameraViewModel
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.LastPhotoPreview
import androidx.compose.foundation.layout.Box
import androidx.compose.samples.crane.R
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import de.yanneckreiss.cameraxtutorial.ui.features.camera.photo_capture.LottieLoadingAnimation

@Composable
fun ExploreSection(
    widthSize: WindowWidthSizeClass,
    modifier: Modifier = Modifier,
    title: String,
    exploreList: List<ExploreModel>,
    onItemClicked: OnExploreItemClicked,
    cameraViewModel: CameraViewModel
) {

    val cameraState: CameraState by cameraViewModel.state.collectAsStateWithLifecycle()
    val apiResponse by cameraViewModel.apiResponse.collectAsStateWithLifecycle()
    val isLoading by cameraViewModel.isLoading.collectAsStateWithLifecycle()

    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(200.dp),
            modifier = Modifier.fillMaxWidth().imePadding(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            cameraState.capturedImage?.let {
                item {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LastPhotoPreview(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(start = 5.dp, bottom = 10.dp), // Adjusted modifier
                            lastCapturedPhoto = it
                        )
                    }
                }
            }
            item {
                Box(modifier = Modifier.fillMaxSize(),contentAlignment = Alignment.Center) {
                    LottieLoadingAnimation(isLoading = isLoading)
                }
            }
            apiResponse?.let { response ->
                // Assuming response is a JSON string, deserialize it
                val detectionResult = response.replace("\\n", "\n")
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        detectionResult.split("\n").forEach { line ->
                            Text(text = line, style = MaterialTheme.typography.body1)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
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
