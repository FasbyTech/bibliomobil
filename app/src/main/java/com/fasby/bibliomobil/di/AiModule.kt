package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.ai.GeminiAiRepositoryImpl
import com.fasby.bibliomobil.domain.ai.AiRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAiRepository(
        geminiAiRepositoryImpl: GeminiAiRepositoryImpl
    ): AiRepository
}
