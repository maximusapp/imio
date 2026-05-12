package com.globaldevmax.app.imio

import android.app.Application
import com.globaldevmax.app.imio.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ImioApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@ImioApplication)
            modules(appModule)
        }
    }
}
