package com.fasby.bibliomobil.data.remote.api

import com.fasby.bibliomobil.data.remote.dto.GoogleBooksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApiService {

    /**
     * Consulta libros filtrando por metadatos o códigos ISBN masivos.
     * @param query Ejemplo: "isbn:9788411405126" o "Dragon Ball 01"
     */
    @GET("books/v1/volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 1
    ): Response<GoogleBooksResponse>
}
