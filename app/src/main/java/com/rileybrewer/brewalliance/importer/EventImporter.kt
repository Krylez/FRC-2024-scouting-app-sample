package com.rileybrewer.brewalliance.importer

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.rileybrewer.brewalliance.proto.Event
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.FileNotFoundException
import java.io.IOException
import javax.inject.Inject

//@Singleton
class EventImporter @Inject constructor(
    @ApplicationContext context: Context
) {
    private val resolver = context.contentResolver

    suspend fun import(
        uri: Uri
    ): Event {
        return withContext(Dispatchers.Default) {
            try {
                resolver.query(
                    uri, null, null, null, null, null
                )?.use { cursor ->
                    cursor.moveToFirst()

//                    val size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE))

                    resolver.openInputStream(uri)?.use { inputStream ->
//                        val bytes = ByteArray(size.toInt())
//                        val bis = BufferedInputStream(inputStream)
//                        val dis = DataInputStream(bis)
//                        dis.readFully(bytes)
                        return@withContext Event.parseFrom(inputStream)
                    }
                    cursor.close()
                }
            } catch (e: FileNotFoundException ) {
                Log.e("EventImporter", "File not found.", e)
            } catch (e: IOException ) {
                Log.e("EventImporter", "IO Error.", e)
            }
            return@withContext Event.getDefaultInstance()
        }
    }
}
