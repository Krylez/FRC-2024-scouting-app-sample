package com.rileybrewer.brewalliance.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.events.CurrentEventViewModel
import com.rileybrewer.brewalliance.events.DistrictEventViewModel
import com.rileybrewer.brewalliance.importer.EventImportButton
import com.rileybrewer.brewalliance.proto.Event
import com.rileybrewer.brewalliance.proto.Settings
import com.rileybrewer.brewalliance.reports.ReportViewModel
import com.rileybrewer.brewalliance.settings.SettingsViewModel
import kotlinx.coroutines.flow.map

/** Application default home screen. */
@Composable
fun Home(
    onViewEvents: () -> Unit,
    onImportReport: () -> Unit,
    onViewMatches: () -> Unit,
    onViewReports: () -> Unit,
    onViewSettings: () -> Unit,
    eventViewModel: CurrentEventViewModel = hiltViewModel(),
    districtEventViewModel: DistrictEventViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val reportCount by reportViewModel.reports.map {
        it.reportsMap.size
    }.collectAsStateWithLifecycle(
        0
    )
    val eventCount by districtEventViewModel.events.map {
        it.eventsList.size
    }.collectAsStateWithLifecycle(
        0
    )
    val currentEvent by eventViewModel.event.collectAsStateWithLifecycle(
        initialValue = Event.getDefaultInstance()
    )
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle(
        initialValue = Settings.getDefaultInstance()
    )

    Column(
        Modifier.fillMaxWidth()
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Reports",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 8.dp)
            )
            Text(
                text = "$reportCount reports on device",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(all = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Button(
                    onClick = onViewReports
                ) {
                    Text("View")
                }
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = onImportReport
                ) {
                    Text("Import New")
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Current Event",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 8.dp)
            )
            Text(
                text = if (currentEvent == Event.getDefaultInstance()) {
                    "No Current Event"
                } else {
                    currentEvent.name
                },
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(all = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Button(
                    enabled = currentEvent != Event.getDefaultInstance(),
                    onClick = onViewMatches
                ) {
                    Text("View Matches")
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "District Events",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 8.dp)
            )
            Text(
                text = "$eventCount events on device",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(all = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Button(
                    onClick = onViewEvents
                ) {
                    Text("View")
                }
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Button(
                    onClick = {
                        districtEventViewModel.sync()
                    }
                ) {
                    Text("Sync with TBA")
                }
            }
        }
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(all = 8.dp)
            )
            Text(
                text = "Device: ${settings.deviceKey}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )
            Text(
                text = "Scout: ${settings.scoutKey}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                Button(
                    onClick = onViewSettings
                ) {
                    Text("View")
                }
            }
        }
    }
}
