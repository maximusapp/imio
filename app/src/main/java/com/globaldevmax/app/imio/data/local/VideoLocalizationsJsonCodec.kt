package com.globaldevmax.app.imio.data.local

import com.globaldevmax.app.imio.domain.model.VideoLocalization
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

internal object VideoLocalizationsJsonCodec {
    private val gson = Gson()
    private val listType = object : TypeToken<List<VideoLocalization>>() {}.type

    fun encode(localizations: List<VideoLocalization>): String? {
        if (localizations.isEmpty()) return null
        return gson.toJson(localizations)
    }

    fun decode(json: String?): List<VideoLocalization> {
        if (json.isNullOrBlank()) return emptyList()
        return gson.fromJson(json, listType) ?: emptyList()
    }
}
