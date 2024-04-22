package com.rileybrewer.brewalliance.importer

import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.rileybrewer.brewalliance.reports.ReportViewModel
import com.rileybrewer.brewalliance.proto.Report
import com.rileybrewer.brewalliance.proto.Settings
import com.rileybrewer.brewalliance.settings.SettingsViewModel

/** Used to display the UI for importing a report. */
@kotlin.OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReportImportScreen(
    onComplete: () -> Unit,
    reportViewModel: ReportViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var report by remember { mutableStateOf(Report.getDefaultInstance()) }
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle(
        Settings.getDefaultInstance()
    )

    if (cameraPermissionState.status.isGranted) {
        if (report == Report.getDefaultInstance()) {
            ScanScreen {
                report = if (it.scoutKey.isNotEmpty()) {
                    it
                } else {
                    // Assume we need to complete the report, so it's ours
                    it.toBuilder()
                        .setScoutKey(settings.scoutKey)
                        .setDeviceKey(settings.deviceKey)
                        .build()
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Report details:",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Scout: ${report.scoutKey}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Match: ${report.matchKey}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Text(
                    text = "Team: ${report.teamKey}",
                    style = MaterialTheme.typography.headlineSmall
                )
                Button(
                    onClick = {
                        reportViewModel.saveReport(report)
                        onComplete()
                    }
                ) {
                    Text("Import")
                }
            }
        }
    } else {
        Column {
            val textToShow = if (cameraPermissionState.status.shouldShowRationale) {
                "QR code scanning will not be possible without using the camera."
            } else {
                "To scan a QR code, this app must use the camera"
            }
            Text(textToShow)
            Button(
                onClick = { cameraPermissionState.launchPermissionRequest() }
            ) {
                Text("Request permission")
            }
        }
    }
}

/** Displays the camera QR code scanner. */
@OptIn(ExperimentalGetImage::class)
@Composable
fun ScanScreen(
    onReport: (Report) -> Unit
) {
    val context = LocalContext.current
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraController = remember { LifecycleCameraController(context) }
    val executor = remember { ContextCompat.getMainExecutor(context) }

    val barcodeScanner: BarcodeScanner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build()
        )
    }

    cameraController.setImageAnalysisAnalyzer(
        executor,
        MlKitAnalyzer(
            listOf(barcodeScanner),
            COORDINATE_SYSTEM_VIEW_REFERENCED,
            executor
        ) { result: MlKitAnalyzer.Result? ->
            val barcodeResults = result?.getValue(barcodeScanner)
            if ((barcodeResults == null) ||
                (barcodeResults.size == 0) ||
                (barcodeResults.first() == null)
            ) {
                return@MlKitAnalyzer
            }

            onReport(Report.parseFrom(barcodeResults[0].rawBytes))
        }
    )

    cameraController.bindToLifecycle(LocalLifecycleOwner.current)
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
    }
}
