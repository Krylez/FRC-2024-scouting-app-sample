package com.rileybrewer.brewalliance.service

import com.rileybrewer.brewalliance.service.json.EventJson
import com.rileybrewer.brewalliance.service.json.MatchJson
import com.rileybrewer.brewalliance.service.json.MediaJson
import com.rileybrewer.brewalliance.service.json.TeamJson
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit service API for The Blue Alliance. The implementation of this interface is generated.
 */
interface BlueAllianceService {

    /**
     * Gets a list of all events for a district key.
     */
    @GET("district/{district_key}/events/simple")
    suspend fun getEvents(@Path("district_key") districtKey: String): List<EventJson>

    /**
     * Gets a list of all matches for an event key.
     */
    @GET("event/{event_key}/matches/simple")
    suspend fun getMatches(@Path("event_key") eventKey: String): List<MatchJson>

    /**
     * Gets a list of all teams for an event key.
     */
    @GET("event/{event_key}/teams/simple")
    suspend fun getTeams(@Path("event_key") eventKey: String): List<TeamJson>

    /**
     * Gets a list of all media for a team key.
     */
    @GET("team/{team_key}/media/2024")
    suspend fun teamMediaList(@Path("team_key") teamKey: String): List<MediaJson>
}
