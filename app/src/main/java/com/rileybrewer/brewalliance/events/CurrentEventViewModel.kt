package com.rileybrewer.brewalliance.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rileybrewer.brewalliance.proto.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrentEventViewModel @Inject constructor(
    private val eventRepository: EventRepository
): ViewModel() {
    val event = eventRepository.event

    /**
     * Fetch all content for the given event, overwriting the current event.
     */
    fun syncEventContents(event: Event) {
        viewModelScope.launch {
            eventRepository.delete()
            eventRepository.syncEventContents(event)
        }
    }

    /** Overwrite current event with the given value. */
    fun updateEvent(event: Event) {
        viewModelScope.launch {
            eventRepository.update(event)
        }
    }
}
