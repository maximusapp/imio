package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.BuildConfig
import com.globaldevmax.app.imio.network.api.VideosApiService
import com.globaldevmax.app.imio.network.auth.DigestAuthenticator
import com.globaldevmax.app.imio.network.connectivity.ConnectivityChecker
import com.globaldevmax.app.imio.network.interceptor.KeepDataMediaLoggingInterceptor
import com.globaldevmax.app.imio.network.interceptor.KeepDataRequestInterceptor
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

        val keepDataUsername = BuildConfig.KEEPDATA_USERNAME
        val keepDataPassword = BuildConfig.KEEPDATA_PASSWORD

        OkHttpClient.Builder()
            .authenticator(
                DigestAuthenticator(
                    username = keepDataUsername,
                    password = keepDataPassword
                )
            )
            .addInterceptor(
                KeepDataRequestInterceptor(
                    username = keepDataUsername,
                    password = keepDataPassword
                )
            )
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(KeepDataMediaLoggingInterceptor())
                }
            }
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
