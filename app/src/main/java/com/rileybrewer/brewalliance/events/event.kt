package com.rileybrewer.brewalliance.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.proto.DistrictEvents
import com.rileybrewer.brewalliance.proto.Event
import java.text.SimpleDateFormat

/** Displays the details for event. */
@Composable
fun EventCard(
    event: Event,
    onClick: (Event) -> Unit
) {
    val outputFormat = SimpleDateFormat("MMMM d")
    val inputFormat = SimpleDateFormat("yyyy-MM-dd")
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .clickable { onClick.invoke(event) }
    ) {
        Text(
            text = event.name,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = outputFormat.format(inputFormat.parse(event.startDate)),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = "${event.city}, ${event.stateProv}",
            style = MaterialTheme.typography.titleSmall
        )
    }
}

/** Displays screen for events. */
@Composable
fun EventScreen(
    eventClick: (Event) -> Unit,
    currentEventViewModel: CurrentEventViewModel = hiltViewModel(),
    districtEventViewModel: DistrictEventViewModel = hiltViewModel()
) {
    val currentEvent by currentEventViewModel.event.collectAsStateWithLifecycle(initialValue = Event.getDefaultInstance())
    val events by districtEventViewModel.events.collectAsStateWithLifecycle(DistrictEvents.getDefaultInstance())
    var confirmEvent by remember { mutableStateOf(Event.getDefaultInstance()) }

    if (confirmEvent != Event.getDefaultInstance()) {
        ConfirmationDialog(
            currentEvent = currentEvent,
            onDismissRequest = {
                confirmEvent = Event.getDefaultInstance()
            },
            onConfirmation = {
                currentEventViewModel.syncEventContents(confirmEvent)
                eventClick(confirmEvent)
                confirmEvent = Event.getDefaultInstance()
            }
        )
    }
    EventList(
        events = events,
        eventClick = { event ->
            if (currentEvent == Event.getDefaultInstance()) {
                currentEventViewModel.syncEventContents(event)
                confirmEvent = Event.getDefaultInstance()
                eventClick.invoke(event)
            } else if(event.key != currentEvent.key) {
                confirmEvent = event
            } else {
                eventClick.invoke(event)
            }
        },
        refreshClick = { districtEventViewModel.sync() }
    )
}

/** Displays a list of events */
@Composable
fun EventList(
    events: DistrictEvents,
    eventClick: (Event) -> Unit,
    refreshClick: () -> Unit
) {
    LazyColumn {
        items(events.eventsList.sortedBy { it.startDate }) { event ->
            EventCard(event, eventClick)
        }
        if (events.eventsList.size == 0) {
            item {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 4.dp),
                    onClick = refreshClick
                ) {
                    Text("Sync events from TBA")
                }
            }
        }
    }
}

/** Confirmation shown when current event data is about to be overwritten. */
@Composable
fun ConfirmationDialog(
    currentEvent: Event,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        icon = {
            Icon(Icons.Default.Info, contentDescription = "")
        },
        title = {
            Text("Warning")
        },
        text = {
            Text("Opening this event will overwrite the current event (${currentEvent.name}).")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmation
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Dismiss")
            }
        }
    )
}
