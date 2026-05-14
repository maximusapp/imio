package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.core.network.ConnectivityChecker
import com.globaldevmax.app.imio.core.parent.ParentModeStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { ConnectivityChecker(androidContext()) }
    single { ParentModeStore(androidContext()) }
}
