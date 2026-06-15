package com.globaldevmax.app.imio.di

import com.globaldevmax.app.imio.ui.screen.favorite.FavoriteViewModel
import com.globaldevmax.app.imio.ui.screen.home.HomeViewModel
import com.globaldevmax.app.imio.ui.screen.premium.PremiumViewModel
import com.globaldevmax.app.imio.ui.screen.search.SearchViewModel
import com.globaldevmax.app.imio.ui.screen.video.VideoViewModel
import com.globaldevmax.app.imio.ui.screen.videolocale.VideoLocaleSetupViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { FavoriteViewModel(get(), get(), get(), get(), get()) }
    viewModel { (fromProfile: Boolean) ->
        VideoLocaleSetupViewModel(fromProfile, get(), get())
    }
    viewModel { PremiumViewModel(get()) }
    viewModel { (videoId: String) ->
        VideoViewModel(
            videoId = videoId,
            getVideoByIdUseCase = get(),
            getCachedVideosUseCase = get(),
            observePreferredVideoLocaleUseCase = get(),
            eveningModeStore = get(),
            parentModeStore = get(),
            premiumRepository = get(),
            okHttpClient = get()
        )
    }
}
