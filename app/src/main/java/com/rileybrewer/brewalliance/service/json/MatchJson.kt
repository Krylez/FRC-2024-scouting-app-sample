package com.rileybrewer.brewalliance.service.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MatchJson(
    val key: String,
    val comp_level: String,
    val match_number: Int,
    val set_number: Int,
    val alliances: AlliancesJson
)

@JsonClass(generateAdapter = true)
data class AlliancesJson(
    val blue: AllianceJson,
    val red: AllianceJson
)

@JsonClass(generateAdapter = true)
data class AllianceJson(
    val team_keys: List<String>
)