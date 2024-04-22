package com.rileybrewer.brewalliance.events

import android.util.Log
import androidx.datastore.core.DataStore
import com.rileybrewer.brewalliance.proto.CompLevel
import com.rileybrewer.brewalliance.proto.Event
import com.rileybrewer.brewalliance.proto.Match
import com.rileybrewer.brewalliance.proto.Team
import com.rileybrewer.brewalliance.service.json.MatchJson
import com.rileybrewer.brewalliance.service.json.TeamJson
import java.io.IOException
import javax.inject.Inject

/** Data source accessing the currently selected event. */
class EventDataSource @Inject constructor(
    private val event: DataStore<Event>
) {
    /** The currently selected (device) event. */
    val currentEvent = event.data

    /** Overwrite the current event with the provided event. */
    suspend fun updateEvent(
        newEvent: Event
    ) {
        try {
            event.updateData { newEvent }
        } catch (ioException: IOException) {
            Log.e("EventDataSource", "Failed to update current event.", ioException)
        }
    }

    /**
     * Translates the team JSON and match JSON into protos, then merges all the content into
     * the provided event, which is used to overwrite the currently selected event.
     */
    suspend fun update(
        newEvent: Event,
        teamJsons: List<TeamJson>,
        matchJsons: List<MatchJson>
    ) {
        try {
            val teamMap = teamJsons.map {
                Team.newBuilder()
                    .setKey(it.key)
                    .setNickname(it.nickname)
                    .setTeamNumber(it.team_number)
                    .setCity(it.city)
                    .setStateProv(it.state_prov)
                    .build()
            }.associateBy { it.key }

            event.updateData {
                newEvent.toBuilder()
                    .addAllMatches(
                        matchJsons.map { match ->
                            Match.newBuilder()
                                .setKey(match.key)
                                .setCompLevel(
                                    when (match.comp_level) {
                                        "qm" -> CompLevel.QUALIFICATION
                                        "qf" -> CompLevel.QUARTER_FINAL
                                        "sf" -> CompLevel.SEMI_FINAL
                                        "f" -> CompLevel.FINAL
                                        "ef" -> CompLevel.EIGHT_FINAL
                                        else -> CompLevel.UNRECOGNIZED
                                    }
                                )
                                .setMatchNumber(match.match_number)
                                .setSetNumber(match.set_number)
                                .addAllBlueAlliance(
                                    match.alliances.blue.team_keys.map {
                                        teamMap[it] ?: Team.getDefaultInstance()
                                    }
                                )
                                .addAllRedAlliance(
                                    match.alliances.red.team_keys.map {
                                        teamMap[it] ?: Team.getDefaultInstance()
                                    }
                                )
                                .build()
                        }
                    )
                    .build()
            }
        } catch (ioException: IOException) {
            Log.e("EventDataSource", "Failed to update current event.", ioException)
        }
    }

    /** Delete the currently selected event. */
    suspend fun delete() {
        try {
            event.updateData {
                Event.getDefaultInstance()
            }
        } catch (ioException: IOException) {
            Log.e("EventDataSource", "Failed to delete current event.", ioException)
        }
    }
}
