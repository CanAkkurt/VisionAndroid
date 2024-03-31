package de.yanneckreiss.cameraxtutorial

import android.app.Application
import de.yanneckreiss.cameraxtutorial.Api.ApiService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


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
    val networkModule = module {
        single {
            Retrofit.Builder()
                .baseUrl("http://localhost/") // Replace with your actual base URL
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}