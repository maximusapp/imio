package com.globaldevmax.app.imio.network.dto

import com.google.gson.annotations.SerializedName

data class VideoLocalizationDto(
    @SerializedName("locale")
    val locale: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String? = null
)
