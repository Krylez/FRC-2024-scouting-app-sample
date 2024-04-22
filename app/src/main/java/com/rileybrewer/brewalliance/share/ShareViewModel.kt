package com.rileybrewer.brewalliance.share

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    qrCodeGenerator: QrCodeGenerator
): ViewModel() {

    var shareText by mutableStateOf("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val qrCode: StateFlow<Bitmap> = snapshotFlow { shareText }
        .flatMapLatest {
            flow {
                emit(
                    if (it.isNotEmpty()) {
                        qrCodeGenerator.getQrCodeBitmap(it)
                    } else {
                        dummy
                    }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = dummy
        )

    companion object {
        val dummy = Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
    }
}
