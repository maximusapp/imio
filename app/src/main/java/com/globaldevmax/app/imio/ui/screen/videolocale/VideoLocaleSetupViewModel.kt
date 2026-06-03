package com.globaldevmax.app.imio.ui.screen.videolocale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globaldevmax.app.imio.core.locale.VideoLocaleStore
import com.globaldevmax.app.imio.core.preferences.VideoContentLocale
import com.globaldevmax.app.imio.domain.usecase.ObservePreferredVideoLocaleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoLocaleSetupViewModel(
    private val fromProfile: Boolean,
    observePreferredVideoLocaleUseCase: ObservePreferredVideoLocaleUseCase,
    private val videoLocaleStore: VideoLocaleStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        VideoLocaleSetupUiState(fromProfile = fromProfile)
    )
    val uiState: StateFlow<VideoLocaleSetupUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePreferredVideoLocaleUseCase().collect { savedLocale ->
                _uiState.update { state ->
                    state.copy(selectedLocale = savedLocale ?: state.selectedLocale)
                }
            }
        }
    }

    fun onLocaleSelected(locale: String) {
        if (locale !in VideoContentLocale.SUPPORTED) return
        _uiState.update { it.copy(selectedLocale = locale) }
    }

    fun saveSelectedLocale(onSaved: () -> Unit) {
        val locale = _uiState.value.selectedLocale ?: return
        viewModelScope.launch {
            videoLocaleStore.setPreferredVideoLocale(locale)
            onSaved()
        }
    }
}

data class VideoLocaleSetupUiState(
    val fromProfile: Boolean,
    val selectedLocale: String? = null
) {
    val canContinue: Boolean
        get() = selectedLocale != null
}
