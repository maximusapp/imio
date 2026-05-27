package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.data.billing.PlayBillingPremiumRepository
import com.globaldevmax.app.imio.data.repository.FavoriteRepositoryImpl
import com.globaldevmax.app.imio.data.repository.VideoRepositoryImpl
import com.globaldevmax.app.imio.domain.repository.FavoriteRepository
import com.globaldevmax.app.imio.domain.repository.PremiumRepository
import com.globaldevmax.app.imio.domain.repository.VideoRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single<VideoRepository> { VideoRepositoryImpl(get()) }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get()) }
    single<PremiumRepository> { PlayBillingPremiumRepository(androidContext()) }
}
