package com.rileybrewer.brewalliance.service.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventJson(
    val key: String,
    val name: String,
    val start_date: String,
    val city: String,
    val state_prov: String,
)