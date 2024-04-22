package com.rileybrewer.brewalliance.service.json

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaJson(
    val details: Details,
    val direct_url: String,
    val foreign_key: String,
    val preferred: Boolean,
    val type: String,
    val view_url: String
)

@JsonClass(generateAdapter = true)
data class Details(
    val base64Image: String = ""
)