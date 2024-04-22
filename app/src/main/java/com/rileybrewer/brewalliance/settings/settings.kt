package com.rileybrewer.brewalliance.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.proto.Settings

@Composable
fun Settings(
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle(initialValue = Settings.getDefaultInstance())

    var scoutInput by remember { mutableStateOf(false) }
    var deviceInput by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(all = 8.dp)
    ) {
        Header("Scout Identifier")
        Text(
            text = settings.scoutKey.ifEmpty { "UNKNOWN" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable { scoutInput = true },
            style = MaterialTheme.typography.titleMedium,
        )
        if (scoutInput) {
            TextDialog(
                title = "Scout Identifier",
                initialValue = settings.scoutKey,
                onDismiss = { scoutInput = false }
            ) {
                scoutInput = false
                settingsViewModel.saveSettings(
                    settings.toBuilder().setScoutKey(it).build()
                )
            }
        }
        Header("Device Identifier")
        Text(
            text = settings.deviceKey.ifEmpty { "UNKNOWN" },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp)
                .clickable { deviceInput = true },
            style = MaterialTheme.typography.titleMedium,
        )
        if (deviceInput) {
            TextDialog(
                title = "Device Identifier",
                initialValue = settings.deviceKey,
                onDismiss = { deviceInput = false }
            ) {
                deviceInput = false
                settingsViewModel.saveSettings(
                    settings.toBuilder().setDeviceKey(it).build()
                )
            }
        }
    }
}

@Composable
fun Header(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(all = 8.dp),
            text = text,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun TextDialog(
    title: String,
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputValue by remember { mutableStateOf(initialValue) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(shape = MaterialTheme.shapes.medium) {
            Column {
                Column(Modifier.padding(24.dp)) {
                    Text(
                        style = MaterialTheme.typography.headlineSmall,
                        text = title
                    )
                    Spacer(Modifier.size(16.dp))
                    TextField(
                        value = inputValue,
                        singleLine = true,
                        onValueChange = {
                            inputValue = it
                        }
                    )
                }
                Spacer(Modifier.size(4.dp))
                Row(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    Arrangement.aligned(Alignment.End),
                ) {
                    TextButton(onClick = { onConfirm(inputValue) }) {
                        Text("Ok")
                    }
                }
            }
        }
    }
}
