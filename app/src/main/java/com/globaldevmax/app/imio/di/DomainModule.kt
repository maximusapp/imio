package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.domain.usecase.GetVideoByIdUseCase
import com.globaldevmax.app.imio.domain.usecase.GetVideosUseCase
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoriteIdsUseCase
import com.globaldevmax.app.imio.domain.usecase.ObserveFavoritesUseCase
import com.globaldevmax.app.imio.domain.usecase.ToggleFavoriteUseCase
import org.koin.dsl.module

val domainModule = module {
    factory { GetVideosUseCase(get()) }
    factory { GetVideoByIdUseCase(get(), get()) }
    factory { ObserveFavoriteIdsUseCase(get()) }
    factory { ObserveFavoritesUseCase(get()) }
    factory { ToggleFavoriteUseCase(get()) }
}
