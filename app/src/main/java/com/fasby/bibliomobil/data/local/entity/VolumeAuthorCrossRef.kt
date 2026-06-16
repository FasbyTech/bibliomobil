package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "volume_author_cross_ref",
    primaryKeys = ["isbn", "authorId"],
    indices = [Index(value = ["authorId"])]
)
data class VolumeAuthorCrossRef(
    val isbn: String,
    val authorId: String
)
