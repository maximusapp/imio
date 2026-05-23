package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.ui.screen.favorite.FavoriteViewModel
import com.globaldevmax.app.imio.ui.screen.home.HomeViewModel
import com.globaldevmax.app.imio.ui.screen.video.VideoViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get(), get()) }
    viewModel { (videoId: String) ->
        VideoViewModel(
            videoId = videoId,
            getVideoByIdUseCase = get(),
            getCachedVideosUseCase = get(),
            okHttpClient = get()
        )
    }
}
