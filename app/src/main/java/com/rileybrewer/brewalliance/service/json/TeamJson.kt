package com.rileybrewer.brewalliance.service.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TeamJson(
    val key: String,
    val city: String,
    val state_prov: String,
    val team_number: Int,
    val nickname: String
) {
    companion object {
        val NONE = TeamJson("", "", "", 0, "")
    }
}
