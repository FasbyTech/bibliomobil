package com.fasby.bibliomobil.data.local.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.fasby.bibliomobil.data.local.entity.AuthorEntity
import com.fasby.bibliomobil.data.local.entity.CollectionEntity
import com.fasby.bibliomobil.data.local.entity.LoanEntity
import com.fasby.bibliomobil.data.local.entity.VolumeAuthorCrossRef
import com.fasby.bibliomobil.data.local.entity.VolumeEntity

data class DetailedVolume(
    @Embedded 
    val volume: VolumeEntity,

    @Relation(
        parentColumn = "collection_id",
        entityColumn = "id"
    )
    val collection: CollectionEntity?,

    @Relation(
        parentColumn = "isbn",
        entityColumn = "id",
        associateBy = Junction(
            value = VolumeAuthorCrossRef::class,
            parentColumn = "isbn",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>,

    @Relation(
        parentColumn = "isbn",
        entityColumn = "isbn"
    )
    val loanHistory: List<LoanEntity> = emptyList()
)
