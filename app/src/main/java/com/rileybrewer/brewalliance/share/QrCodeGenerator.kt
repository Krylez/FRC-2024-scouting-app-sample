package com.rileybrewer.brewalliance.share

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.rileybrewer.brewalliance.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import qrcode.QRCode
import qrcode.color.Colors
import java.io.ByteArrayOutputStream
import javax.inject.Inject

/** Used to generate QR code. */
class QrCodeGenerator @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val qrCodeWriter = QRCodeWriter()

    /** Generate a QR code bitmaps from the provided string. */
    suspend fun getQrCodeBitmap(qrCodeContent: String): Bitmap {
        return withContext(Dispatchers.Default) {
            return@withContext decodeOther(qrCodeContent)
        }
    }

    private fun decodeZXing(qrCodeContent: String): Bitmap {
        //pixels
        val size = 512

        val bits = qrCodeWriter.encode(
            qrCodeContent,
            BarcodeFormat.QR_CODE,
            size,
            size,
            // Make the QR code buffer border narrower
            mapOf(EncodeHintType.MARGIN to 1)
        )
        return Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.RGB_565
        ).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(
                        x,
                        y,
                        if (bits[x, y]) {
                            Color.BLACK
                        } else {
                            Color.WHITE
                        }
                    )
                }
            }
        }
    }

    private fun decodeOther(qrCodeContent: String): Bitmap {
        val logo = BitmapFactory.decodeResource(context.resources, R.raw.qr_logo)
        val stream = ByteArrayOutputStream()
        logo.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = QRCode.ofSquares()
            .withGradientColor(
                Colors.BLACK,
                Colors.DARK_RED
            )
            .withSize(100)
            .withLogo(stream.toByteArray(), logo.width, logo.height)
            .build(qrCodeContent)
            .render()
            .getBytes()

        stream.close()
        logo.recycle()

        return BitmapFactory.decodeByteArray(
            bytes,
            0,
            bytes.size
        )
    }
}
