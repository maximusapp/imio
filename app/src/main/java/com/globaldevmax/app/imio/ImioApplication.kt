package com.globaldevmax.app.imio

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.imageDecoderEnabled
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.globaldevmax.app.imio.di.appModule
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
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

        configureMobileAds()
        MobileAds.initialize(this)
    }

    private fun configureMobileAds() {
        val requestConfiguration = RequestConfiguration.Builder()
            .setTagForChildDirectedTreatment(
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
            )
            .setTagForUnderAgeOfConsent(
                RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
            )
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)
    }

    override fun newImageLoader(context: android.content.Context): ImageLoader {
        val okHttpClient = getKoin().get<OkHttpClient>()
        return ImageLoader.Builder(context)
            // BitmapFactory decodes some JPEG variants more reliably than ImageDecoder (API 29+).
            .imageDecoderEnabled(false)
            .components {
                add(OkHttpNetworkFetcherFactory(okHttpClient))
            }
            .build()
    }
}
