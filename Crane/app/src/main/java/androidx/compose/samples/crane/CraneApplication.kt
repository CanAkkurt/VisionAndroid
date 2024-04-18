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

package androidx.compose.samples.crane

import android.app.Application
import androidx.compose.samples.crane.Api.ApiService
import androidx.compose.samples.crane.util.UnsplashSizingInterceptor
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.compose.AsyncImage
import com.cloudinary.android.MediaManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@HiltAndroidApp
class CraneApplication : Application(), ImageLoaderFactory {

    /**
     * Create the singleton [ImageLoader].
     * This is used by [AsyncImage] to load images in the app.
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                add(UnsplashSizingInterceptor)
            }
            .build()
    }
    override fun onCreate() {
        super.onCreate()
        // Configure Cloudinary
        val config: MutableMap<String, String> = HashMap()
        config["cloud_name"] = "drspxfuvc"
        config["api_key"] = "199446471633162"
        config["api_secret"] = "EcoaEDRqEmbgWgokeuFVyV-v-A4"
        MediaManager.init(this, config)
    }

    @Module
    @InstallIn(SingletonComponent::class)
    object AppModule {


        @Singleton
        @Provides
        fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()


        @Singleton
        @Provides
        fun provideRetrofit(okHttpClient: OkHttpClient): ApiService = Retrofit.Builder()
            .baseUrl("http://192.168.178.87:8098/apiV1/") // Your base URL
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)


    }

}
