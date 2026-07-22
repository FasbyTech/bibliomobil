package com.fasby.bibliomobil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenLibraryResponse(
    @SerializedName("docs") val docs: List<OpenLibraryDoc>?
)

data class OpenLibraryDoc(
    @SerializedName("key") val key: String?,
    @SerializedName("title") val title: String,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("first_publish_year") val firstPublishYear: Int?,
    @SerializedName("isbn") val isbn: List<String>?,
    @SerializedName("cover_i") val coverId: Long?
) {
    fun getCoverUrl(): String? = coverId?.let { "https://covers.openlibrary.org/b/id/$it-M.jpg" }
}
