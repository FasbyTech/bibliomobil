package com.fasby.bibliomobil.data.local.dao

import androidx.room.*
import com.fasby.bibliomobil.data.local.entity.*
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import kotlinx.coroutines.flow.Flow

@Dao
interface VolumeDao {

    // ========================================================================
    // INSERCIONES Y BORRADOS (Escritura)
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolume(volume: VolumeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: CollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVolumeAuthorCrossRef(crossRef: VolumeAuthorCrossRef)

    /**
     * Una transacción atómica para insertar un volumen completo con sus autores.
     * Si falla la inserción de un autor, se revierte toda la operación.
     */
    @Transaction
    suspend fun insertCompleteVolume(
        volume: VolumeEntity, 
        authors: List<AuthorEntity>
    ) {
        insertVolume(volume)
        authors.forEach { author ->
            insertAuthor(author)
            insertVolumeAuthorCrossRef(
                VolumeAuthorCrossRef(isbn = volume.isbn, authorId = author.id)
            )
        }
    }

    @Delete
    suspend fun deleteVolume(volume: VolumeEntity)

    @Update
    suspend fun updateVolume(volume: VolumeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: LoanEntity)

    @Update
    suspend fun updateLoan(loan: LoanEntity)

    @Query("SELECT * FROM loans WHERE id = :loanId LIMIT 1")
    suspend fun getLoanById(loanId: String): LoanEntity?

    @Query("SELECT * FROM loans WHERE isbn = :isbn ORDER BY loanDate DESC")
    fun getLoansForVolume(isbn: String): Flow<List<LoanEntity>>

    // ========================================================================
    // CONSULTAS REACTIVAS (Lectura mediante Flow)
    // ========================================================================

    /**
     * Obtiene el catálogo completo ordenado por fecha de adición.
     * Al devolver [DetailedVolume], Room resolverá automáticamente las relaciones
     * con la colección y los autores subyacentes en paralelo.
     */
    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    fun getAllDetailedVolumes(): Flow<List<DetailedVolume>>

    @Transaction
    @Query("SELECT * FROM volumes ORDER BY createdAt DESC")
    suspend fun getAllDetailedVolumesStatic(): List<DetailedVolume>

    /**
     * Obtiene un volumen específico por su clave primaria (ISBN).
     */
    @Transaction
    @Query("SELECT * FROM volumes WHERE isbn = :isbn LIMIT 1")
    suspend fun getVolumeByIsbn(isbn: String): DetailedVolume?

    /**
     * Filtra volúmenes que pertenecen a una colección concreta.
     */
    @Transaction
    @Query("SELECT * FROM volumes WHERE collection_id = :collectionId ORDER BY number ASC")
    fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>>

    @Query("SELECT * FROM collections ORDER BY name ASC")
    fun getAllCollections(): Flow<List<CollectionEntity>>

    @Query("SELECT * FROM collections WHERE id = :id LIMIT 1")
    suspend fun getCollectionById(id: String): CollectionEntity?

    // ========================================================================
    // LA JOYA DEL TFM: BÚSQUEDA INDEXADA FTS5
    // ========================================================================

    /**
     * Ejecuta una consulta MATCH de texto completo de alto rendimiento.
     * En lugar de escanear la tabla entera con un LIKE '%texto%', Room consulta
     * el índice invertido virtual de FTS5 y hace un JOIN con la tabla real.
     *
     * @param searchQuery Término formateado para SQLite (ej: "Dragon*")
     */
    @Transaction
    @Query("""
        SELECT * FROM volumes 
        WHERE title LIKE '%' || :searchQuery || '%' 
        OR synopsis LIKE '%' || :searchQuery || '%'
    """)
    fun searchVolumesFts(searchQuery: String): Flow<List<DetailedVolume>>
}
