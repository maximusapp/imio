package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.core.parent.ParentModeStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    includes(networkModule, databaseModule, dataModule, domainModule, viewModelModule)
    single { ParentModeStore(androidContext()) }
}
