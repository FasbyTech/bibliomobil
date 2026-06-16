package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey 
    val id: String, // Usaremos UUID.randomUUID().toString() o slugs
    val name: String,
    val publisher: String, // Editorial (Panini, Planeta, ECC...)
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
