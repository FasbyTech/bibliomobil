package com.fasby.bibliomobil.di

import com.fasby.bibliomobil.data.repository.VolumeRepositoryImpl
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVolumeRepository(
        volumeRepositoryImpl: VolumeRepositoryImpl
    ): VolumeRepository
}
