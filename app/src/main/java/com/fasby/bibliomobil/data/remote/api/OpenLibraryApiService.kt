package com.fasby.bibliomobil.data.remote.api

import com.fasby.bibliomobil.data.remote.dto.OpenLibraryResponse
import com.fasby.bibliomobil.data.remote.dto.OpenLibraryWorkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenLibraryApiService {

    /**
     * Busca libros en Open Library por ISBN o texto general.
     */
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String? = null,
        @Query("isbn") isbn: String? = null,
        @Query("limit") limit: Int = 1
    ): Response<OpenLibraryResponse>

    /**
     * Obtiene los detalles de una obra (work), incluyendo la descripción.
     */
    @GET("{workKey}.json")
    suspend fun getWorkDetails(
        @Path("workKey", encoded = false) workKey: String
    ): Response<OpenLibraryWorkResponse>
}
