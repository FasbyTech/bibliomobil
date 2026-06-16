package com.fasby.bibliomobil.domain.repository

import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.VolumeEntity
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import kotlinx.coroutines.flow.Flow

interface VolumeRepository {
    
    // Transacciones de lectura reactiva
    fun getAllVolumes(): Flow<List<DetailedVolume>>
    
    fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>>
    
    suspend fun getVolumeByIsbn(isbn: String): DetailedVolume?
    
    // Transacción de búsqueda optimizada por FTS5
    fun searchVolumes(query: String): Flow<List<DetailedVolume>>
    
    // Transacciones de escritura
    suspend fun saveCompleteVolume(volume: VolumeEntity, authors: List<AuthorEntity>)
    
    suspend fun saveCollection(collection: CollectionEntity)
    
    suspend fun deleteVolume(volume: VolumeEntity)

    suspend fun updateVolume(volume: VolumeEntity)

    fun getAllCollections(): Flow<List<CollectionEntity>>

    suspend fun getCollectionById(id: String): CollectionEntity?

    // Sincronización remota
    suspend fun searchRemoteBook(query: String): Pair<VolumeEntity, List<AuthorEntity>>?

    // Exportación
    suspend fun generateCsvReport(): String

    // Backup & Restore
    suspend fun clearDatabase()
    suspend fun createBackup(onUriReady: (android.net.Uri) -> Unit)
    suspend fun restoreBackup(uri: android.net.Uri): Boolean
}
