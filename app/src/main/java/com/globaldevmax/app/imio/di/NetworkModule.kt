package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.BuildConfig
import com.globaldevmax.app.imio.network.api.VideosApiService
import com.globaldevmax.app.imio.network.auth.DigestAuthenticator
import com.globaldevmax.app.imio.network.connectivity.ConnectivityChecker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single { ConnectivityChecker(androidContext()) }

    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .authenticator(
                DigestAuthenticator(
                    username = BuildConfig.KEEPDATA_USERNAME,
                    password = BuildConfig.KEEPDATA_PASSWORD
                )
            )
            .addInterceptor(loggingInterceptor)
            .connectTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(NETWORK_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.KEEPDATA_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single<VideosApiService> {
        get<Retrofit>().create(VideosApiService::class.java)
    }
}

private const val NETWORK_TIMEOUT_SECONDS = 30L
