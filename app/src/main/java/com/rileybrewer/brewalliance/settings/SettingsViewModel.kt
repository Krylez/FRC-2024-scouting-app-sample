package com.rileybrewer.brewalliance.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rileybrewer.brewalliance.proto.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataSource: SettingsDataSource
): ViewModel() {

    val settings = settingsDataSource.settings

    fun saveSettings(settings: Settings) {
        viewModelScope.launch {
            settingsDataSource.update(settings)
        }
    }
}
