package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
// import androidx.room.Fts5
import androidx.room.PrimaryKey

// @Fts5(contentEntity = VolumeEntity::class)
@Entity(tableName = "volumes_fts")
data class VolumeFtsEntity(
    @PrimaryKey(autoGenerate = true) val rowid: Int,
    val title: String,
    val synopsis: String
)
