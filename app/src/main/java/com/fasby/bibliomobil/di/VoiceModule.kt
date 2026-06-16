package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.voice.AndroidVoiceRecognizerManager
import com.fasby.bibliomobil.domain.voice.VoiceRecognizerManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VoiceModule {

    @Binds
    @Singleton
    abstract fun bindVoiceRecognizerManager(
        androidVoiceRecognizerManager: AndroidVoiceRecognizerManager
    ): VoiceRecognizerManager
}
