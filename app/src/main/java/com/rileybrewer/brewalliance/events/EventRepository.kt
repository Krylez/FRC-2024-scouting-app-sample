package com.rileybrewer.brewalliance.events

import com.rileybrewer.brewalliance.service.BlueAllianceService
import com.rileybrewer.brewalliance.proto.Event
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val dataSource: EventDataSource,
    private val service: BlueAllianceService
) {
    /** The currently selected (device) event. */
    val event = dataSource.currentEvent

    /**
     * Fetches all content for a desired event, then saves it locally. This will wipe out the
     * existing event content. Content fetched includes, teams, team avatars, and currently
     * available match data.
     */
    suspend fun syncEventContents(event: Event) {
        val teams = service.getTeams(event.key)

        dataSource.update(
            event.toBuilder().let { builder ->
                teams.forEach { team ->
                    builder.putThumbnails(
                        team.key,
                        service.teamMediaList(team.key).find { media ->
                            media.type == "avatar"
                        }?.details?.base64Image ?: ""
                    )
                }
                builder
            }.build(),
            teams,
            service.getMatches(event.key)
        )
    }

    /** Overwrite the current event with the provided event data. */
    suspend fun update(event: Event) {
        dataSource.updateEvent(event)
    }

    /** Deletes all existing event content. */
    suspend fun delete() {
        dataSource.delete()
    }
}
