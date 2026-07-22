package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.remote.api.GoogleBooksApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

                // Inyección dinámica de la API Key en todas las peticiones
                val urlWithKey = originalRequest.url.newBuilder()
                    .setQueryParameter("key", apiKey)
                    .build()
                
                // Configuración de cabeceras de seguridad para restringir el uso de la Key
                val cert = "c90fdadc5ca9c56618328f6695584abcbffdeb29"
                val pkg = "com.fasby.bibliomobil"
                
                val request = originalRequest.newBuilder()
                    .url(urlWithKey)
                    .header("X-Android-Package", pkg)
                    .header("X-Android-Cert", cert)
                    .build()
                
                if (com.fasby.bibliomobil.BuildConfig.DEBUG) {
                    android.util.Log.d("BiblioMobilAuth", "Sending headers - Pkg: $pkg, Cert: $cert")
                }
                
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        }

    @Provides
    @Singleton
    fun provideGoogleBooksApiService(retrofit: Retrofit): GoogleBooksApiService {
        return retrofit.create(GoogleBooksApiService::class.java)
    }
}
