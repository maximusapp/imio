package com.globaldevmax.app.imio.domain.usecase

import com.globaldevmax.app.imio.core.locale.VideoLocaleStore
import kotlinx.coroutines.flow.Flow

class ObservePreferredVideoLocaleUseCase(
    private val videoLocaleStore: VideoLocaleStore
) {
    operator fun invoke(): Flow<String?> = videoLocaleStore.preferredVideoLocale
}
