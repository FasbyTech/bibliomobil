package com.fasby.bibliomobil.data.repository

import com.fasby.bibliomobil.data.local.dao.VolumeDao
import com.fasby.bibliomobil.data.local.database.AppDatabase
import com.fasby.bibliomobil.data.local.entity.*
import com.fasby.bibliomobil.data.local.model.DetailedVolume
import com.fasby.bibliomobil.data.remote.api.GoogleBooksApiService
import com.fasby.bibliomobil.data.remote.api.OpenLibraryApiService
import com.fasby.bibliomobil.domain.repository.VolumeRepository
import com.fasby.bibliomobil.di.IoDispatcher
import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VolumeRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val volumeDao: VolumeDao,
    private val googleBooksApi: GoogleBooksApiService,
    private val openLibraryApi: OpenLibraryApiService,
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VolumeRepository {

    override fun getAllVolumes(): Flow<List<DetailedVolume>> {
        return volumeDao.getAllDetailedVolumes()
    }

    override fun getVolumesByCollection(collectionId: String): Flow<List<DetailedVolume>> {
        return volumeDao.getVolumesByCollection(collectionId)
    }

    override suspend fun getVolumeByIsbn(isbn: String): DetailedVolume? = withContext(ioDispatcher) {
        volumeDao.getVolumeByIsbn(isbn)
    }

    override fun searchVolumes(query: String): Flow<List<DetailedVolume>> {
        val trimmedQuery = query.trim()
        // Si el query está vacío, devolvemos todo
        return if (trimmedQuery.isEmpty()) {
            volumeDao.getAllDetailedVolumes()
        } else {
            volumeDao.searchVolumesFts(trimmedQuery)
        }
    }

    override suspend fun saveCompleteVolume(
        volume: VolumeEntity, 
        authors: List<AuthorEntity>
    ) = withContext(ioDispatcher) {
        volumeDao.insertCompleteVolume(volume, authors)
    }

    override suspend fun saveCollection(collection: CollectionEntity) = withContext(ioDispatcher) {
        volumeDao.insertCollection(collection)
    }

    override suspend fun deleteCollection(collection: CollectionEntity): Unit = withContext(ioDispatcher) {
        volumeDao.deleteCollection(collection)
    }

    override suspend fun deleteVolume(volume: VolumeEntity) = withContext(ioDispatcher) {
        volumeDao.deleteVolume(volume)
    }

    override suspend fun updateVolume(volume: VolumeEntity) = withContext(ioDispatcher) {
        volumeDao.updateVolume(volume)
    }

    override fun getAllCollections(): Flow<List<CollectionEntity>> {
        return volumeDao.getAllCollections()
    }

    override suspend fun getCollectionById(id: String): CollectionEntity? = withContext(ioDispatcher) {
        volumeDao.getCollectionById(id)
    }

    override suspend fun registerLoan(isbn: String, lentTo: String): Unit = withContext(ioDispatcher) {
        volumeDao.insertLoan(LoanEntity(isbn = isbn, lentTo = lentTo))
    }

    override suspend fun markAsReturned(loanId: String): Unit = withContext(ioDispatcher) {
        volumeDao.getLoanById(loanId)?.let { loan ->
             volumeDao.updateLoan(loan.copy(returnDate = System.currentTimeMillis()))
        }
    }

    override fun getLoanHistory(isbn: String): Flow<List<LoanEntity>> {
        return volumeDao.getLoansForVolume(isbn)
    }

    override suspend fun searchRemoteBook(query: String): Pair<VolumeEntity, List<AuthorEntity>>? = withContext(ioDispatcher) {
        try {
            android.util.Log.d("BiblioMobil", "Iniciando búsqueda remota: $query")
            
            // INTENTO 1: Google Books
            val googleResponse = try {
                googleBooksApi.searchBooks(query)
            } catch (e: Exception) {
                android.util.Log.e("BiblioMobil", "Error de red en Google Books: ${e.message}")
                null
            }
            
            var items = if (googleResponse?.isSuccessful == true) googleResponse.body()?.items else null

            // Fallback Google Books: Si es un ISBN, probamos solo el número
            if (items.isNullOrEmpty() && query.startsWith("isbn:")) {
                val cleanIsbn = query.removePrefix("isbn:").trim()
                val response2 = try { googleBooksApi.searchBooks(cleanIsbn, maxResults = 3) } catch (e: Exception) { null }
                items = if (response2?.isSuccessful == true) response2.body()?.items else null
            }

            if (!items.isNullOrEmpty()) {
                val book = items.first().volumeInfo
                android.util.Log.d("BiblioMobil", "¡Éxito en Google Books! Libro encontrado: ${book.title}")
                
                val authors = book.authors?.map { name ->
                    AuthorEntity(id = UUID.randomUUID().toString(), name = name, role = "Autor")
                } ?: emptyList()

                val remoteIsbn = book.industryIdentifiers?.find { it.type == "ISBN_13" }?.identifier
                    ?: book.industryIdentifiers?.find { it.type == "ISBN_10" }?.identifier
                    ?: query.filter { it.isDigit() }.ifBlank { UUID.randomUUID().toString() }

                val volume = VolumeEntity(
                    isbn = remoteIsbn,
                    collectionId = null,
                    title = book.title,
                    number = 0,
                    publishedYear = book.publishedDate?.take(4)?.toIntOrNull() ?: 0,
                    synopsis = book.description ?: "",
                    coverPath = book.imageLinks?.thumbnail?.replace("http:", "https:") ?: "",
                    rating = 0,
                    isRead = false
                )
                return@withContext Pair(volume, authors)
            }

            // INTENTO 2: Open Library (Fallback si Google falla o no tiene resultados)
            android.util.Log.d("BiblioMobil", "Google Books falló o no dio resultados. Probando Open Library...")
            val cleanIsbnOnly = query.removePrefix("isbn:").trim()
            
            // Intento A: Búsqueda general
            var olResponse = try {
                openLibraryApi.searchBooks(query = cleanIsbnOnly)
            } catch (e: Exception) {
                null
            }

            // Intento B: Si el A falla, búsqueda específica por campo ISBN
            if (olResponse?.isSuccessful != true || olResponse.body()?.docs.isNullOrEmpty()) {
                android.util.Log.d("BiblioMobil", "Open Library búsqueda general sin éxito. Probando campo ISBN específico...")
                olResponse = try {
                    openLibraryApi.searchBooks(isbn = cleanIsbnOnly)
                } catch (e: Exception) {
                    null
                }
            }

                val olDocs = if (olResponse?.isSuccessful == true) olResponse.body()?.docs else null
                if (!olDocs.isNullOrEmpty()) {
                    val doc = olDocs.first()
                    android.util.Log.d("BiblioMobil", "¡Éxito en Open Library! Libro encontrado: ${doc.title}")

                    // Intentamos obtener la sinopsis desde la Work Key
                    var remoteSynopsis = ""
                    doc.key?.let { key ->
                        try {
                            android.util.Log.d("BiblioMobil", "Buscando descripción ampliada en OL: $key")
                            val workResponse = openLibraryApi.getWorkDetails(key.removePrefix("/"))
                            if (workResponse.isSuccessful) {
                                remoteSynopsis = workResponse.body()?.getDescriptionText() ?: ""
                                android.util.Log.d("BiblioMobil", "Sinopsis OL encontrada (${remoteSynopsis.length} chars)")
                            }
                        } catch (e: Exception) {
                            android.util.Log.w("BiblioMobil", "No se pudo obtener descripción de OL: ${e.message}")
                        }
                    }

                    val authors = doc.authorName?.map { name ->
                        AuthorEntity(id = UUID.randomUUID().toString(), name = name, role = "Autor")
                    } ?: emptyList()

                    val volume = VolumeEntity(
                        isbn = doc.isbn?.firstOrNull() ?: cleanIsbnOnly,
                        collectionId = null,
                        title = doc.title,
                        number = 0,
                        publishedYear = doc.firstPublishYear ?: 0,
                        synopsis = remoteSynopsis,
                        coverPath = doc.getCoverUrl() ?: "",
                        rating = 0,
                        isRead = false
                    )
                    return@withContext Pair(volume, authors)
                }

            android.util.Log.w("BiblioMobil", "No se encontraron resultados en ninguna fuente para: $query")
            null
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error fatal en búsqueda remota", e)
            null
        }
    }

    override suspend fun generateCsvReport(): String = withContext(ioDispatcher) {
        val volumes = volumeDao.getAllDetailedVolumesStatic()
        val header = "ISBN,Titulo,Autores,Año,Leido\n"
        val rows = volumes.joinToString("\n") { detailed ->
            val volume = detailed.volume
            val authors = detailed.authors.joinToString(";") { it.name }
            "${volume.isbn},\"${volume.title}\",\"$authors\",${volume.publishedYear},${if (volume.isRead) "SI" else "NO"}"
        }
        header + rows
    }

    override suspend fun clearDatabase() = withContext(ioDispatcher) {
        database.clearAllTables()
    }

    /**
     * Implementación de la creación de backup físico del archivo SQLite.
     * Utiliza un checkpoint de WAL para garantizar la integridad de los datos.
     */
    override suspend fun createBackup(onUriReady: (Uri) -> Unit): Unit = withContext(ioDispatcher) {
        try {
            // Forzamos un checkpoint para que todo el contenido de WAL pase al .db principal
            try {
                database.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").moveToFirst()
            } catch (e: Exception) {
                android.util.Log.w("BiblioMobil", "WAL checkpoint failed or DB not ready, continuing with file copy", e)
            }

            val dbFile = context.getDatabasePath("biblio_mobil_db")
            if (dbFile.exists()) {
                // Copiamos el archivo de la base de datos a la caché para poder compartirlo
                val backupFile = File(context.cacheDir, "backup_bibliomobil_${System.currentTimeMillis()}.db")
                dbFile.copyTo(backupFile, overwrite = true)
                
                val contentUri = FileProvider.getUriForFile(
                    context,
                    "com.fasby.bibliomobil.fileprovider",
                    backupFile
                )
                onUriReady(contentUri)
            } else {
                android.util.Log.e("BiblioMobil", "Database file not found at ${dbFile.absolutePath}")
            }
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error fatal creando backup", e)
        }
    }

    /**
     * Restaura la base de datos reemplazando el archivo actual por uno externo.
     * Requiere el reinicio de la aplicación tras la operación.
     */
    override suspend fun restoreBackup(uri: Uri): Boolean = withContext(ioDispatcher) {
        try {
            database.close()
            val dbFile = context.getDatabasePath("biblio_mobil_db")
            context.contentResolver.openInputStream(uri)?.use { input ->
                dbFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            android.util.Log.e("BiblioMobil", "Error restaurando backup", e)
            false
        }
    }
}
