package com.fasby.bibliomobil.data.remote.dto

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class OpenLibraryWorkResponse(
    @SerializedName("description") val description: JsonElement? // Puede ser un String o un objeto con un campo "value"
) {
    fun getDescriptionText(): String? {
        if (description == null) return null
        return if (description.isJsonPrimitive) {
            description.asString
        } else if (description.isJsonObject) {
            description.asJsonObject.get("value")?.asString
        } else {
            null
        }
    }
}
