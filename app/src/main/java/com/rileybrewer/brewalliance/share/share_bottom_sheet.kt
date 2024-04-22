package com.rileybrewer.brewalliance.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Display a bottom-sheet dialog for displaying QR codes. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareBottomSheet(
    shareText: String,
    onDismiss: () -> Unit,
    shareViewModel: ShareViewModel = hiltViewModel()
) {
    val qrCode by shareViewModel.qrCode.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()

    shareViewModel.shareText = shareText

    if (shareText.isNotEmpty()) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState
        ) {
            if (qrCode.width == 1) {
                CircularProgressIndicator(
                    modifier = Modifier.size(size = 250.dp)
                        .padding(all = 64.dp)
                        .align(Alignment.CenterHorizontally)
                )
            } else {
                Image(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    bitmap = qrCode.asImageBitmap(),
                    contentDescription = shareViewModel.shareText,
                )
            }
        }
    }
}
