package com.fasby.bibliomobil.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "volumes",
    foreignKeys = [
        ForeignKey(
            entity = CollectionEntity::class,
            parentColumns = ["id"],
            childColumns = ["collection_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class VolumeEntity(
    @PrimaryKey 
    val isbn: String, // El código de barras o un ID autogenerado si no tiene
    @ColumnInfo(name = "collection_id", index = true) 
    val collectionId: String?, // Nullable si es un tomo único (One-shot)
    val title: String,
    val number: Int, // Número del tomo/volumen dentro de la colección
    val publishedYear: Int,
    val synopsis: String,
    val coverPath: String, // Ruta local en el almacenamiento del dispositivo para la foto
    val rating: Int, // Puntuación de 1 a 5 estrellas
    val personalReview: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
