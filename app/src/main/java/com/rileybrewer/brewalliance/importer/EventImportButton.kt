package com.rileybrewer.brewalliance.importer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rileybrewer.brewalliance.events.CurrentEventViewModel
import com.rileybrewer.brewalliance.proto.Event

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventImportButton(
    text: String,
    currentEventViewModel: CurrentEventViewModel = hiltViewModel(),
    importerViewModel: ImporterViewModel = hiltViewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val event by importerViewModel.event.collectAsStateWithLifecycle()

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            }
            importerViewModel.importUri = uri
        }
    )

    Button(
        onClick = {
            filePicker.launch(arrayOf("*/*"))
        },
    ) {
        Text(
            text = text
        )
    }
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                importerViewModel.importUri = Uri.EMPTY
            },
            sheetState = sheetState
        ) {
            if (event == Event.getDefaultInstance()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(size = 250.dp)
                        .padding(all = 64.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Text(
                    text = "Event",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(all = 8.dp)
                )
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(all = 8.dp)
                )
                Text(text = "Event")
                Button(
                    onClick = {
                        currentEventViewModel.updateEvent(event)
                        showBottomSheet = false
                    }
                ) {
                    Text(text = "Import")
                }
            }
        }
    }
}
