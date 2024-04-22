package com.rileybrewer.brewalliance.events

import android.util.Log
import androidx.datastore.core.DataStore
import com.rileybrewer.brewalliance.proto.DistrictEvents
import com.rileybrewer.brewalliance.proto.Event
import com.rileybrewer.brewalliance.service.json.EventJson
import java.io.IOException
import javax.inject.Inject

/**
 * Data source accessing a list of district events.
 */
class DistrictEventsDataSource @Inject constructor(
    private val districtEvents: DataStore<DistrictEvents>
) {
    /** The local (device) list of all district events. */
    val events = districtEvents.data

    /**
     * Translate the list of JSON events to proto events, then save them.
     */
    suspend fun update(eventJsons: List<EventJson>) {
        try {
            districtEvents.updateData {
                DistrictEvents.newBuilder()
                    .addAllEvents(
                        eventJsons.map {
                            Event.newBuilder()
                                .setKey(it.key)
                                .setName(it.name)
                                .setCity(it.city)
                                .setStateProv(it.state_prov)
                                .setStartDate(it.start_date)
                                .build()
                        }
                    ).build()
            }
        } catch (ioException: IOException) {
            Log.e("DistrictEventsDataSource", "Failed to update district events.", ioException)
        }
    }
}