package com.globaldevmax.app.imio

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.globaldevmax.app.imio.di.appModule
import okhttp3.OkHttpClient
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ImioApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ImioApplication)
            modules(appModule)
        }
    }

    override fun newImageLoader(context: android.content.Context): ImageLoader {
        val okHttpClient = getKoin().get<OkHttpClient>()
        return ImageLoader.Builder(context)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
            .build()
    }
}
