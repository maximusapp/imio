package com.globaldevmax.app.imio.di

import org.koin.dsl.module

val appModule = module {
    includes(
        dataStoreModule,
        networkModule,
        databaseModule,
        dataModule,
        domainModule,
        viewModelModule
    )
}
