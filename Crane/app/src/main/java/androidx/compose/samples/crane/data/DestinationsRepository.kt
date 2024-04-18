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

package androidx.compose.samples.crane.data

import androidx.compose.samples.crane.Api.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log

class DestinationsRepository @Inject constructor(
    private val apiService: ApiService,
    private val destinationsLocalDataSource: DestinationsLocalDataSource
) {

//    private var _images = mutableListOf<ApiService.ApiResponse>() // Use ApiResponse directly if it fits your needs
//    val images: List<ApiService.ApiResponse> get() = _images

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    val destinations: List<ExploreModel> = destinationsLocalDataSource.craneDestinations
    val hotels: List<ExploreModel> = destinationsLocalDataSource.craneHotels
    val restaurants: List<ExploreModel> = destinationsLocalDataSource.craneRestaurants
    val cities: List<City> = listCities



    fun getDestination(cityName: String): City? {
        return cities.firstOrNull {
            it.name == cityName
        }
    }

    suspend fun fetchImages(): List<ApiService.ApiResponse> {
        // Ideally handle errors and exceptions here as well
        return apiService.fetchImages()
    }
}
