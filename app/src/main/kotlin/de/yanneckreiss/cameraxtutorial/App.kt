package de.yanneckreiss.cameraxtutorial

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(defaultModule)
            modules(listOf(networkModule))
        }
    }
    val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Increase connect timeout
        .readTimeout(30, TimeUnit.SECONDS)    // Increase read timeout
        .writeTimeout(30, TimeUnit.SECONDS)   // Increase write timeout
        .build()


    val networkModule = module {
        single {
            Retrofit.Builder()
                .baseUrl("http://192.168.178.87:8098/apiV1/") // Replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                .build()
                .create(ApiService::class.java)
        }
    }



}