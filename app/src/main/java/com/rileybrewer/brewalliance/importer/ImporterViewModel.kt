package com.rileybrewer.brewalliance.importer

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rileybrewer.brewalliance.proto.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ImporterViewModel @Inject constructor(
    private val eventImporter: EventImporter
): ViewModel() {
    var importUri by mutableStateOf(Uri.EMPTY)

    @OptIn(ExperimentalCoroutinesApi::class)
    val event: StateFlow<Event> = snapshotFlow { importUri }
        .flatMapLatest { uri ->
            flow {
                emit(
                    if (uri != Uri.EMPTY) {
                        eventImporter.import(uri)
                    } else {
                        Event.getDefaultInstance()
                    }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Event.getDefaultInstance()
        )
}
