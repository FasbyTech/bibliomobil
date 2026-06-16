package com.fasby.bibliomobil.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GoogleBooksResponse(
    @SerializedName("items") val items: List<BookItem>?
)

data class BookItem(
    @SerializedName("volumeInfo") val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    @SerializedName("title") val title: String,
    @SerializedName("authors") val authors: List<String>?,
    @SerializedName("publishedDate") val publishedDate: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageLinks") val imageLinks: ImageLinks?,
    @SerializedName("industryIdentifiers") val industryIdentifiers: List<IndustryIdentifier>?
)

data class IndustryIdentifier(
    @SerializedName("type") val type: String,
    @SerializedName("identifier") val identifier: String
)

data class ImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?
)
