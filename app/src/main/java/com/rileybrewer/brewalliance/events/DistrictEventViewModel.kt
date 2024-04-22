package com.rileybrewer.brewalliance.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DistrictEventViewModel @Inject constructor(
    private val districtEventsRepository: DistrictEventsRepository
): ViewModel() {
    val events = districtEventsRepository.districtEvents

    fun sync() {
        viewModelScope.launch {
            districtEventsRepository.syncDistrictEvents()
        }
    }
}
