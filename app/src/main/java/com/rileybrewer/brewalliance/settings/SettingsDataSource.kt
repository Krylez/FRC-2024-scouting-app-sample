package com.rileybrewer.brewalliance.settings

import android.util.Log
import androidx.datastore.core.DataStore
import com.rileybrewer.brewalliance.proto.Settings
import java.io.IOException
import javax.inject.Inject

/** Data source accessing the current device settings. */
class SettingsDataSource @Inject constructor(
    private val dataStore: DataStore<Settings>
) {
    /** Current device settings. */
    val settings = dataStore.data

    /** Overwrites the current settings with the provided ones. */
    suspend fun update(settings: Settings) {
        try {
            dataStore.updateData { settings }
        } catch (ioException: IOException) {
            Log.e("SettingsDataSource", "Failed to update settings.", ioException)
        }
    }
}