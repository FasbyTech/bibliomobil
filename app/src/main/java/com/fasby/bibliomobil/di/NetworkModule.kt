package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.remote.api.GoogleBooksApiService
import com.fasby.bibliomobil.data.remote.api.OpenLibraryApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (com.fasby.bibliomobil.BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val originalRequest = chain.request()
                val apiKey = com.fasby.bibliomobil.BuildConfig.GOOGLE_BOOKS_API_KEY

                // Si es Google Books, añadimos la clave. Si es OpenLibrary, no.
                val request = if (originalRequest.url.host.contains("googleapis")) {
                    val urlWithKey = originalRequest.url.newBuilder()
                        .addQueryParameter("key", apiKey)
                        .build()
                    originalRequest.newBuilder()
                        .url(urlWithKey)
                        .build()
                } else {
                    originalRequest
                }
                
                var response = chain.proceed(request)
                var tryCount = 0
                val maxRetries = 2

                // Si es un 503 o 504 (errores temporales), reintentamos
                while (!response.isSuccessful && (response.code == 503 || response.code == 504) && tryCount < maxRetries) {
                    android.util.Log.w("BiblioMobilNet", "Error ${response.code} detectado. Reintentando... ($tryCount)")
                    tryCount++
                    response.close()
                    Thread.sleep(1500L) 
                    response = chain.proceed(request)
                }
                
                response
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    @Named("GoogleRetrofit")
    fun provideGoogleRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("OpenLibraryRetrofit")
    fun provideOpenLibraryRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://openlibrary.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApiService(@Named("GoogleRetrofit") retrofit: Retrofit): GoogleBooksApiService {
        return retrofit.create(GoogleBooksApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOpenLibraryApiService(@Named("OpenLibraryRetrofit") retrofit: Retrofit): OpenLibraryApiService {
        return retrofit.create(OpenLibraryApiService::class.java)
    }
}
