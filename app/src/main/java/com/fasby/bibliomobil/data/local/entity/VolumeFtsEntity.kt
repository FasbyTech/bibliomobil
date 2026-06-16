package com.fasby.bibliomobil.data.local.entity

import androidx.room.Entity
import androidx.room.Fts4

@Fts4(contentEntity = VolumeEntity::class)
@Entity(tableName = "volumes_fts")
data class VolumeFtsEntity(
    val title: String,
    val synopsis: String
)
