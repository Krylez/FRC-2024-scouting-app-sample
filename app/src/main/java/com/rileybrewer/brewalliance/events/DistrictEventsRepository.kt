package com.rileybrewer.brewalliance.events

import com.rileybrewer.brewalliance.service.BlueAllianceService
import javax.inject.Inject

class DistrictEventsRepository @Inject constructor(
    private val dataSource: DistrictEventsDataSource,
    private val service: BlueAllianceService
) {
    /** The list of all (device) events. */
    val districtEvents = dataSource.events

    /**
     * Fetches all events for a district, then saves them locally.
     */
    suspend fun syncDistrictEvents() {
        dataSource.update(service.getEvents("2024pnw"))
    }
}
